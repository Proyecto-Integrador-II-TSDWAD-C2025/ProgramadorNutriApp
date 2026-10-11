package com.example.nutriappmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutriappmovil.adapter.ComidaPlanAdapter;
import com.example.nutriappmovil.adapter.MenuPlanAlimenticio;
import com.example.nutriappmovil.model.AsignacionPlan;
import com.example.nutriappmovil.model.MiPlanAlimenticioResponse;
import com.example.nutriappmovil.model.PlanDetalle;
import com.example.nutriappmovil.network.ApiClient;
import com.example.nutriappmovil.network.PlanAlimenticioApiService;
import com.example.nutriappmovil.session.SessionManager;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity "Mi Plan Alimenticio". Hija de DashboardActivity.
 * Sprint 2: muestra el plan real del usuario (GET /api/mi-plan-alimenticio/)
 * con sus comidas agrupadas por día.
 *
 * Estados de la pantalla:
 * - Cargando.
 * - Plan con menú, o plan sin comidas cargadas (vacío).
 * - Sin perfil (404): ofrece ir a completarlo.
 * - En revisión: Django manda "asignacion" en null y un mensaje.
 * - Error de red o del servidor, con Reintentar.
 * - Sesión inválida (401): limpia la sesión y vuelve al Login.
 */
public class MiPlanAlimenticioActivity extends AppCompatActivity {

    private View layoutCargando;
    private View layoutEstado;
    private View layoutPlan;

    private TextView tvEstadoTitulo;
    private TextView tvEstadoMensaje;
    private Button btnEstadoAccion;

    private TextView tvNombrePlan;
    private TextView tvDescripcionPlan;
    private TextView tagCaloriasPlan;
    private TextView tagDuracionPlan;
    private TextView tagOrigenPlan;
    private TextView tvVigenciaPlan;
    private TextView tvMotivoPlan;
    private TextView tvMenuVacio;
    private RecyclerView recyclerComidas;

    private ComidaPlanAdapter adapter;
    private SessionManager sessionManager;
    private PlanAlimenticioApiService planApiService;
    private Call<MiPlanAlimenticioResponse> llamadaPlan;

    // Se activa al ir a Perfil para volver a pedir el plan al regresar
    private boolean recargarAlVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_plan_alimenticio);

        inicializarVistas();

        sessionManager = new SessionManager(this);
        planApiService = ApiClient
                .getClient(this)
                .create(PlanAlimenticioApiService.class);

        adapter = new ComidaPlanAdapter();
        recyclerComidas.setLayoutManager(new LinearLayoutManager(this));
        recyclerComidas.setAdapter(adapter);

        findViewById(R.id.btnVolverPlanAlimenticio).setOnClickListener(v -> finish());

        cargarPlan();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (recargarAlVolver) {
            recargarAlVolver = false;
            cargarPlan();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (llamadaPlan != null) {
            llamadaPlan.cancel();
        }
    }

    private void inicializarVistas() {
        layoutCargando = findViewById(R.id.layoutCargandoPlan);
        layoutEstado = findViewById(R.id.layoutEstadoPlan);
        layoutPlan = findViewById(R.id.layoutPlan);

        tvEstadoTitulo = findViewById(R.id.tvEstadoTitulo);
        tvEstadoMensaje = findViewById(R.id.tvEstadoMensaje);
        btnEstadoAccion = findViewById(R.id.btnEstadoAccion);

        tvNombrePlan = findViewById(R.id.tvNombrePlan);
        tvDescripcionPlan = findViewById(R.id.tvDescripcionPlan);
        tagCaloriasPlan = findViewById(R.id.tagCaloriasPlan);
        tagDuracionPlan = findViewById(R.id.tagDuracionPlan);
        tagOrigenPlan = findViewById(R.id.tagOrigenPlan);
        tvVigenciaPlan = findViewById(R.id.tvVigenciaPlan);
        tvMotivoPlan = findViewById(R.id.tvMotivoPlan);
        tvMenuVacio = findViewById(R.id.tvMenuVacio);
        recyclerComidas = findViewById(R.id.recyclerComidas);

        // Encabezados para la navegación de TalkBack
        ViewCompat.setAccessibilityHeading(findViewById(R.id.tvTituloPlanAlimenticio), true);
        ViewCompat.setAccessibilityHeading(tvEstadoTitulo, true);
        ViewCompat.setAccessibilityHeading(tvNombrePlan, true);
        ViewCompat.setAccessibilityHeading(findViewById(R.id.tvTituloMenu), true);
    }

    /** Pide el plan a /api/mi-plan-alimenticio/ y actualiza la pantalla. */
    private void cargarPlan() {
        if (llamadaPlan != null) {
            llamadaPlan.cancel();
        }

        mostrarCargando();

        llamadaPlan = planApiService.getMiPlan();
        llamadaPlan.enqueue(new Callback<MiPlanAlimenticioResponse>() {
            @Override
            public void onResponse(
                    Call<MiPlanAlimenticioResponse> call,
                    Response<MiPlanAlimenticioResponse> response
            ) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) {
                    return;
                }

                if (response.code() == 401) {
                    // Token inválido, vencido o revocado
                    volverAlLogin();
                } else if (response.code() == 404) {
                    // Django responde 404 cuando el usuario no completó su perfil
                    mostrarSinPerfil();
                } else if (!response.isSuccessful() || response.body() == null) {
                    mostrarError(getString(R.string.plan_alim_error_servidor, response.code()));
                } else {
                    mostrarRespuesta(response.body());
                }
            }

            @Override
            public void onFailure(Call<MiPlanAlimenticioResponse> call, Throwable t) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) {
                    return;
                }

                // IOException: sin conexión o tiempo de espera agotado.
                // Cualquier otra cosa: la respuesta no tenía el formato esperado.
                mostrarError(getString(
                        t instanceof IOException
                                ? R.string.plan_alim_error_red
                                : R.string.plan_alim_error_inesperado
                ));
            }
        });
    }

    private void mostrarRespuesta(MiPlanAlimenticioResponse respuesta) {
        AsignacionPlan asignacion = respuesta.getAsignacion();

        if (asignacion == null || asignacion.getPlan() == null) {
            String mensaje = respuesta.getMensaje();
            mostrarEstado(
                    R.string.plan_alim_revision_titulo,
                    estaVacio(mensaje) ? getString(R.string.plan_alim_revision_mensaje) : mensaje,
                    R.string.plan_alim_actualizar,
                    v -> cargarPlan()
            );
            return;
        }

        mostrarPlan(asignacion);
    }

    private void mostrarCargando() {
        layoutCargando.setVisibility(View.VISIBLE);
        layoutEstado.setVisibility(View.GONE);
        layoutPlan.setVisibility(View.GONE);
    }

    private void mostrarPlan(AsignacionPlan asignacion) {
        PlanDetalle plan = asignacion.getPlan();

        layoutCargando.setVisibility(View.GONE);
        layoutEstado.setVisibility(View.GONE);
        layoutPlan.setVisibility(View.VISIBLE);

        tvNombrePlan.setText(plan.getNombrePlan());
        mostrarOEsconder(tvDescripcionPlan, plan.getDescripcion());

        tagCaloriasPlan.setText(getString(
                R.string.plan_alim_calorias_dia,
                MenuPlanAlimenticio.formatearCantidad(plan.getCaloriasObjetivo())
        ));
        tagDuracionPlan.setText(getResources().getQuantityString(
                R.plurals.plan_alim_duracion_dias,
                plan.getDuracionDias(),
                plan.getDuracionDias()
        ));
        tagOrigenPlan.setText(
                "profesional".equals(asignacion.getOrigen())
                        ? R.string.plan_alim_origen_profesional
                        : R.string.plan_alim_origen_orientativo
        );

        mostrarOEsconder(
                tvVigenciaPlan,
                estaVacio(asignacion.getFechaInicio()) || estaVacio(asignacion.getFechaFin())
                        ? null
                        : getString(
                                R.string.plan_alim_vigencia,
                                MenuPlanAlimenticio.formatearFecha(asignacion.getFechaInicio()),
                                MenuPlanAlimenticio.formatearFecha(asignacion.getFechaFin())
                        )
        );
        mostrarOEsconder(tvMotivoPlan, asignacion.getMotivo());

        List<Object> items = MenuPlanAlimenticio.agruparPorDia(plan.getComidasPlan());
        adapter.setItems(items);

        // Estado vacío: el plan existe pero todavía no tiene comidas
        boolean sinComidas = items.isEmpty();
        tvMenuVacio.setVisibility(sinComidas ? View.VISIBLE : View.GONE);
        recyclerComidas.setVisibility(sinComidas ? View.GONE : View.VISIBLE);
    }

    private void mostrarSinPerfil() {
        mostrarEstado(
                R.string.plan_alim_sin_perfil_titulo,
                getString(R.string.plan_alim_sin_perfil_mensaje),
                R.string.plan_alim_ir_a_perfil,
                v -> {
                    recargarAlVolver = true;
                    startActivity(new Intent(this, PerfilActivity.class));
                }
        );
    }

    private void mostrarError(String mensaje) {
        mostrarEstado(
                R.string.plan_alim_error_titulo,
                mensaje,
                R.string.plan_alim_reintentar,
                v -> cargarPlan()
        );
    }

    /** Pantalla sin contenido: un título, una explicación y una única acción. */
    private void mostrarEstado(
            int titulo,
            String mensaje,
            int textoBoton,
            View.OnClickListener accion
    ) {
        layoutCargando.setVisibility(View.GONE);
        layoutPlan.setVisibility(View.GONE);

        tvEstadoTitulo.setText(titulo);
        tvEstadoMensaje.setText(mensaje);
        btnEstadoAccion.setText(textoBoton);
        btnEstadoAccion.setOnClickListener(accion);

        layoutEstado.setVisibility(View.VISIBLE);
    }

    private void volverAlLogin() {
        sessionManager.clearSession();
        Toast.makeText(this, R.string.plan_alim_sesion_expirada, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private static void mostrarOEsconder(TextView vista, String texto) {
        if (estaVacio(texto)) {
            vista.setVisibility(View.GONE);
        } else {
            vista.setText(texto);
            vista.setVisibility(View.VISIBLE);
        }
    }

    private static boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
