package com.example.nutriappmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutriappmovil.adapter.PlanAdapter;
import com.example.nutriappmovil.model.PageResponse;
import com.example.nutriappmovil.model.Plan;
import com.example.nutriappmovil.model.Usuario;
import com.example.nutriappmovil.network.ApiClient;
import com.example.nutriappmovil.network.ApiService;
import com.example.nutriappmovil.network.PlanesApiService;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Listado de planes (GET /api/planes/, paginado de a 10).
 *
 * Estados: carga, vacío, error y éxito.
 * Primero consulta /api/me/ para conocer el rol real; solo "administrador" y
 * "nutricionista" ven los botones de alta, edición y baja. Eso es una comodidad
 * de la interfaz: Django vuelve a exigir el permiso (403) en cada pedido.
 * Es hija de DashboardActivity.
 */
public class PlanesActivity extends AppCompatActivity implements PlanAdapter.OnPlanAccionListener {

    private static final int REQ_FORMULARIO = 1001;

    private ApiService apiService;
    private PlanesApiService planesService;
    private PlanAdapter adapter;

    private RecyclerView recyclerPlanes;
    private ProgressBar progressPlanes;
    private View layoutVacioPlanes;
    private View layoutErrorPlanes;
    private TextView tvVacioPlanes;
    private TextView tvErrorPlanes;
    private Button btnNuevoPlan;
    private Button btnCargarMas;

    private Call<Usuario> llamadaMe;
    private Call<PageResponse<Plan>> llamadaLista;
    private Call<Void> llamadaEliminar;

    private boolean puedeGestionar = false;
    private boolean hayMas = false;
    private boolean eliminando = false;

    // Página que se pide con "Cargar más"
    private int paginaSiguiente = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planes);

        recyclerPlanes = findViewById(R.id.recyclerPlanes);
        progressPlanes = findViewById(R.id.progressPlanes);
        layoutVacioPlanes = findViewById(R.id.layoutVacioPlanes);
        layoutErrorPlanes = findViewById(R.id.layoutErrorPlanes);
        tvVacioPlanes = findViewById(R.id.tvVacioPlanes);
        tvErrorPlanes = findViewById(R.id.tvErrorPlanes);
        btnNuevoPlan = findViewById(R.id.btnNuevoPlan);
        btnCargarMas = findViewById(R.id.btnCargarMas);

        apiService = ApiClient.getClient(this).create(ApiService.class);
        planesService = ApiClient.getClient(this).create(PlanesApiService.class);

        adapter = new PlanAdapter(this, this);
        recyclerPlanes.setLayoutManager(new LinearLayoutManager(this));
        recyclerPlanes.setAdapter(adapter);

        findViewById(R.id.btnVolverPlanes).setOnClickListener(v -> finish());
        findViewById(R.id.btnReintentarPlanes).setOnClickListener(v -> cargarInicial());
        btnNuevoPlan.setOnClickListener(v ->
                startActivityForResult(new Intent(this, PlanFormActivity.class), REQ_FORMULARIO));
        btnCargarMas.setOnClickListener(v -> {
            if (hayMas) {
                cargarPagina(paginaSiguiente);
            }
        });

        cargarInicial();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (llamadaMe != null) {
            llamadaMe.cancel();
        }
        if (llamadaLista != null) {
            llamadaLista.cancel();
        }
        if (llamadaEliminar != null) {
            llamadaEliminar.cancel();
        }
    }

    /** Pide el rol real (/api/me/) y después la primera página de planes. */
    private void cargarInicial() {
        mostrarCargando();

        llamadaMe = apiService.me();
        llamadaMe.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (response.code() == 401) {
                    SesionUtil.volverAlLoginPorSesionExpirada(PlanesActivity.this);
                } else if (!response.isSuccessful() || response.body() == null) {
                    mostrarError("No pudimos verificar tu rol (código " + response.code() + ").");
                } else {
                    Usuario usuario = response.body();
                    String rol = usuario.getRol() != null ? usuario.getRol().getNombreRol() : null;

                    puedeGestionar = "administrador".equals(rol) || "nutricionista".equals(rol);
                    adapter.setPuedeGestionar(puedeGestionar);
                    btnNuevoPlan.setVisibility(puedeGestionar ? View.VISIBLE : View.GONE);

                    cargarPagina(1);
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) {
                    return;
                }
                mostrarError("No hay conexión con el servidor. Revisá tu internet e intentá de nuevo.");
            }
        });
    }

    /** Pide una página del listado. La página 1 reemplaza la lista; las demás se agregan al final. */
    private void cargarPagina(int pagina) {
        if (pagina == 1) {
            mostrarCargando();
        } else {
            btnCargarMas.setEnabled(false);
            btnCargarMas.setText("Cargando…");
        }

        llamadaLista = planesService.listarPlanes(pagina);
        llamadaLista.enqueue(new Callback<PageResponse<Plan>>() {
            @Override
            public void onResponse(Call<PageResponse<Plan>> call, Response<PageResponse<Plan>> response) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (response.code() == 401) {
                    SesionUtil.volverAlLoginPorSesionExpirada(PlanesActivity.this);
                    return;
                }
                if (response.code() == 403) {
                    mostrarError("No tenés permiso para ver los planes.");
                    return;
                }
                if (!response.isSuccessful() || response.body() == null) {
                    if (pagina == 1) {
                        mostrarError("No pudimos cargar los planes (código " + response.code() + ").");
                    } else {
                        restaurarBotonCargarMas();
                        Toast.makeText(PlanesActivity.this,
                                "No se pudieron cargar más planes. Intentá de nuevo.", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                PageResponse<Plan> pageResponse = response.body();
                List<Plan> resultados = pageResponse.getResults();
                boolean sinResultados = resultados == null || resultados.isEmpty();

                if (pagina == 1) {
                    if (sinResultados) {
                        mostrarVacio();
                        return;
                    }
                    adapter.reemplazar(resultados);
                } else if (!sinResultados) {
                    adapter.agregar(resultados);
                }

                hayMas = pageResponse.hasNext();
                paginaSiguiente = pagina + 1;
                mostrarLista();
                restaurarBotonCargarMas();
            }

            @Override
            public void onFailure(Call<PageResponse<Plan>> call, Throwable t) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) {
                    return;
                }

                if (pagina == 1) {
                    mostrarError("No hay conexión con el servidor. Revisá tu internet e intentá de nuevo.");
                } else {
                    restaurarBotonCargarMas();
                    Toast.makeText(PlanesActivity.this,
                            "No hay conexión. No se pudieron cargar más planes.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // ---------- Estados de pantalla ----------

    private void mostrarCargando() {
        progressPlanes.setVisibility(View.VISIBLE);
        recyclerPlanes.setVisibility(View.GONE);
        layoutVacioPlanes.setVisibility(View.GONE);
        layoutErrorPlanes.setVisibility(View.GONE);
        btnCargarMas.setVisibility(View.GONE);
    }

    private void mostrarLista() {
        progressPlanes.setVisibility(View.GONE);
        recyclerPlanes.setVisibility(View.VISIBLE);
        layoutVacioPlanes.setVisibility(View.GONE);
        layoutErrorPlanes.setVisibility(View.GONE);
    }

    private void mostrarVacio() {
        progressPlanes.setVisibility(View.GONE);
        recyclerPlanes.setVisibility(View.GONE);
        layoutErrorPlanes.setVisibility(View.GONE);
        btnCargarMas.setVisibility(View.GONE);

        tvVacioPlanes.setText(puedeGestionar
                ? "Todavía no hay planes. Tocá «Nuevo plan» para crear el primero."
                : "No hay planes disponibles por ahora.");
        layoutVacioPlanes.setVisibility(View.VISIBLE);
    }

    private void mostrarError(String mensaje) {
        progressPlanes.setVisibility(View.GONE);
        recyclerPlanes.setVisibility(View.GONE);
        layoutVacioPlanes.setVisibility(View.GONE);
        btnCargarMas.setVisibility(View.GONE);

        tvErrorPlanes.setText(mensaje);
        layoutErrorPlanes.setVisibility(View.VISIBLE);
    }

    private void restaurarBotonCargarMas() {
        btnCargarMas.setEnabled(true);
        btnCargarMas.setText("Cargar más planes");
        btnCargarMas.setVisibility(hayMas ? View.VISIBLE : View.GONE);
    }

    // ---------- Acciones sobre un plan ----------

    @Override
    public void onEditar(Plan plan) {
        Intent intent = new Intent(this, PlanFormActivity.class);
        intent.putExtra(PlanFormActivity.EXTRA_PLAN_JSON, new Gson().toJson(plan));
        startActivityForResult(intent, REQ_FORMULARIO);
    }

    @Override
    public void onEliminar(Plan plan) {
        if (eliminando) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar plan")
                .setMessage("¿Eliminar «" + plan.getNombrePlan() + "»? "
                        + "También se va a quitar de los usuarios que lo tengan asignado y no se puede deshacer.\n\n"
                        + "Si solo querés ocultarlo, editalo y desactivá «Plan activo».")
                .setPositiveButton("Eliminar", (dialogo, cual) -> eliminarPlan(plan))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarPlan(Plan plan) {
        eliminando = true;
        progressPlanes.setVisibility(View.VISIBLE);

        llamadaEliminar = planesService.eliminarPlan(plan.getIdPlan());
        llamadaEliminar.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                eliminando = false;
                progressPlanes.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Toast.makeText(PlanesActivity.this, "Plan eliminado.", Toast.LENGTH_SHORT).show();
                    cargarPagina(1);
                } else if (response.code() == 401) {
                    SesionUtil.volverAlLoginPorSesionExpirada(PlanesActivity.this);
                } else if (response.code() == 403) {
                    Toast.makeText(PlanesActivity.this,
                            "No tenés permiso para eliminar planes.", Toast.LENGTH_LONG).show();
                } else if (response.code() == 404) {
                    Toast.makeText(PlanesActivity.this,
                            "Ese plan ya no existe.", Toast.LENGTH_LONG).show();
                    cargarPagina(1);
                } else {
                    Toast.makeText(PlanesActivity.this,
                            "No se pudo eliminar el plan (código " + response.code() + ").",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) {
                    return;
                }
                eliminando = false;
                progressPlanes.setVisibility(View.GONE);
                Toast.makeText(PlanesActivity.this,
                        "No hay conexión. No se pudo eliminar el plan.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Se creó o editó un plan: volver a pedir la primera página
        if (requestCode == REQ_FORMULARIO && resultCode == RESULT_OK) {
            cargarPagina(1);
        }
    }
}