package com.example.nutriappmovil;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutriappmovil.model.Perfil;
import com.example.nutriappmovil.model.PerfilRequest;
import com.example.nutriappmovil.model.PerfilResponse;
import com.example.nutriappmovil.network.ApiClient;
import com.example.nutriappmovil.network.PerfilApiService;
import com.example.nutriappmovil.perfil.ErroresPerfil;
import com.example.nutriappmovil.perfil.ValidacionPerfil;
import com.example.nutriappmovil.perfil.EstadoCargaPerfil;
import com.example.nutriappmovil.perfil.PerfilRepository;
import com.example.nutriappmovil.perfil.PerfilViewModel;
import com.example.nutriappmovil.perfil.RetrofitPerfilRepository;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {
    private Spinner spSexo, spObjetivo, spActividad, spPreferencia;
    private EditText etEdad, etPesoActual, etAltura, etPesoObjetivo;
    private EditText etDiasEntrenamiento, etLimitaciones, etConsideracionesAlimentarias;
    private Button btnGuardar, btnReintentar;
    private TextView tvEstado;
    private final Map<String, View> campos = new LinkedHashMap<>();
    private PerfilRepository perfilRepository;
    private PerfilViewModel perfilViewModel;
    private Call<PerfilResponse> guardadoCall;
    private boolean restaurandoEdicion;
    private boolean perfilDisponible, cargando, guardando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        inicializarVistas();
        perfilRepository = new RetrofitPerfilRepository(
                ApiClient.getClient(this).create(PerfilApiService.class));
        perfilViewModel = new ViewModelProvider(this,
                new PerfilViewModel.Factory(perfilRepository)).get(PerfilViewModel.class);
        findViewById(R.id.btnVolverPerfil).setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardarPerfil());
        btnReintentar.setOnClickListener(v -> cargarPerfil());

        perfilDisponible = savedInstanceState != null
                && savedInstanceState.getBoolean("perfil_disponible");
        restaurandoEdicion = perfilDisponible;
        perfilViewModel.getEstado().observe(this, estado -> {
            // No sobrescribir el formulario restaurado con la respuesta anterior.
            if (restaurandoEdicion && estado.getTipo() != EstadoCargaPerfil.Tipo.CARGANDO) {
                restaurandoEdicion = false;
                return;
            }
            restaurandoEdicion = false;
            mostrarCarga(estado);
        });
        if (perfilDisponible) {
            actualizarControles();
            mostrarEstado(savedInstanceState.getBoolean("guardando")
                    ? R.string.perfil_guardado_interrumpido : R.string.perfil_listo);
        } else {
            cargarPerfil();
        }
    }

    private void inicializarVistas() {
        spSexo = findViewById(R.id.spSexo);
        spObjetivo = findViewById(R.id.spObjetivo);
        spActividad = findViewById(R.id.spActividad);
        spPreferencia = findViewById(R.id.spPreferencia);
        etEdad = findViewById(R.id.etEdad);
        etPesoActual = findViewById(R.id.etPesoActual);
        etAltura = findViewById(R.id.etAltura);
        etPesoObjetivo = findViewById(R.id.etPesoObjetivo);
        etDiasEntrenamiento = findViewById(R.id.etDiasEntrenamiento);
        etLimitaciones = findViewById(R.id.etLimitaciones);
        etConsideracionesAlimentarias = findViewById(R.id.etConsideracionesAlimentarias);
        btnGuardar = findViewById(R.id.btnGuardarPerfil);
        btnReintentar = findViewById(R.id.btnReintentarPerfil);
        tvEstado = findViewById(R.id.tvEstadoPerfil);
        campos.put("sexo", spSexo);
        campos.put("edad", etEdad);
        campos.put("peso_actual", etPesoActual);
        campos.put("altura_cm", etAltura);
        campos.put("peso_objetivo", etPesoObjetivo);
        campos.put("objetivo", spObjetivo);
        campos.put("actividad", spActividad);
        campos.put("dias_entrenamiento", etDiasEntrenamiento);
        campos.put("preferencia", spPreferencia);
        campos.put("limitaciones", etLimitaciones);
        campos.put("consideraciones_alimentarias", etConsideracionesAlimentarias);
    }

    private void cargarPerfil() {
        restaurandoEdicion = false;
        mostrarCarga(new EstadoCargaPerfil(EstadoCargaPerfil.Tipo.CARGANDO, null));
        perfilViewModel.cargarPerfil();
    }

    private void mostrarCarga(EstadoCargaPerfil estado) {
        cargando = estado.getTipo() == EstadoCargaPerfil.Tipo.CARGANDO;
        perfilDisponible = false;
        btnReintentar.setVisibility(View.GONE);
        switch (estado.getTipo()) {
            case CARGANDO:
                mostrarEstado(R.string.perfil_cargando);
                break;
            case LISTO:
                mostrarPerfil(estado.getPerfil());
                perfilDisponible = true;
                mostrarEstado(R.string.perfil_listo);
                break;
            case NUEVO:
                perfilDisponible = true;
                mostrarEstado(R.string.perfil_nuevo);
                break;
            default:
                mostrarEstado(estado.getTipo() == EstadoCargaPerfil.Tipo.SESION_INVALIDA
                        ? R.string.perfil_sesion_invalida
                        : estado.getTipo() == EstadoCargaPerfil.Tipo.ERROR_CONEXION
                        ? R.string.perfil_error_conexion : R.string.perfil_error_carga);
                btnReintentar.setVisibility(View.VISIBLE);
        }
        actualizarControles();
    }

    private void guardarPerfil() {
        if (!perfilDisponible || cargando || guardando) return;
        for (View campo : campos.values()) {
            if (campo instanceof EditText) ((EditText) campo).setError(null);
        }
        if (!validarNumero(etEdad, 13, 100, false, R.string.perfil_error_edad)
                || !validarNumero(etPesoActual, 30, 300, true, R.string.perfil_error_peso)
                || !validarNumero(etAltura, 100, 250, false, R.string.perfil_error_altura)
                || !validarNumero(etPesoObjetivo, 30, 300, true, R.string.perfil_error_peso)
                || !validarNumero(etDiasEntrenamiento, 1, 6, false, R.string.perfil_error_dias)) {
            mostrarEstado(R.string.perfil_revisar_datos);
            return;
        }
        for (Spinner spinner : new Spinner[] {spSexo, spObjetivo, spActividad, spPreferencia}) {
            if (spinner.getSelectedItemPosition() <= 0) {
                mostrarEstado(R.string.perfil_opciones_obligatorias);
                spinner.requestFocusFromTouch();
                return;
            }
        }
        PerfilRequest request = new PerfilRequest(
                sexo(), Integer.parseInt(texto(etEdad)), decimal(etPesoActual),
                Integer.parseInt(texto(etAltura)), decimal(etPesoObjetivo), objetivo(),
                actividad(), preferencia(), Integer.parseInt(texto(etDiasEntrenamiento)),
                texto(etLimitaciones), texto(etConsideracionesAlimentarias));
        guardando = true;
        actualizarControles();
        mostrarEstado(R.string.perfil_guardando);
        guardadoCall = perfilRepository.actualizarPerfil(request);
        guardadoCall.enqueue(new Callback<PerfilResponse>() {
            @Override public void onResponse(Call<PerfilResponse> call, Response<PerfilResponse> response) {
                if (terminada() || call.isCanceled()) return;
                guardando = false;
                actualizarControles();
                if (respuestaConPerfil(response)) {
                    mostrarPerfil(response.body().getPerfil());
                    mostrarEstado(response.body().isRequiereRevision()
                            ? R.string.perfil_guardado_revision : R.string.perfil_guardado);
                } else if (response.code() == 400) {
                    mostrarEstado(R.string.perfil_revisar_datos);
                    mostrarErroresServidor(response.errorBody());
                } else {
                    if (response.code() == 401) {
                        perfilDisponible = false;
                        actualizarControles();
                    }
                    mostrarEstado(response.code() == 401
                            ? R.string.perfil_sesion_invalida : R.string.perfil_error_guardado);
                }
                cerrarError(response);
            }

            @Override public void onFailure(Call<PerfilResponse> call, Throwable error) {
                if (terminada() || call.isCanceled()) return;
                guardando = false;
                actualizarControles();
                mostrarEstado(R.string.perfil_error_conexion_guardado);
            }
        });
    }

    private boolean validarNumero(EditText campo, int minimo, int maximo, boolean esDecimal, int mensaje) {
        String valor = texto(campo);
        boolean valido = esDecimal ? ValidacionPerfil.decimalEnRango(valor, minimo, maximo)
                : ValidacionPerfil.enteroEnRango(valor, minimo, maximo);
        if (!valido) {
            campo.setError(getString(valor.isEmpty() ? R.string.perfil_campo_obligatorio : mensaje));
            campo.requestFocus();
        }
        return valido;
    }

    private void mostrarErroresServidor(ResponseBody body) {
        if (body == null) return;
        try {
            View primero = null;
            for (Map.Entry<String, String> error : ErroresPerfil.leer(body.string()).entrySet()) {
                View campo = campos.get(error.getKey());
                if (campo instanceof EditText) {
                    ((EditText) campo).setError(error.getValue());
                    if (primero == null) primero = campo;
                } else if (campo instanceof Spinner) {
                    mostrarEstado(getString(R.string.perfil_error_opcion,
                            nombreOpcion(error.getKey()), error.getValue()));
                    if (primero == null) primero = campo;
                }
            }
            if (primero != null) primero.requestFocusFromTouch();
        } catch (IOException ignored) {
            // Conservar el mensaje local si el cuerpo de error no se puede leer.
        }
    }

    private String nombreOpcion(String campo) {
        switch (campo) {
            case "sexo": return getString(R.string.label_sexo);
            case "objetivo": return getString(R.string.label_objetivo);
            case "actividad": return getString(R.string.label_actividad);
            default: return getString(R.string.label_preferencia);
        }
    }

    private void actualizarControles() {
        boolean editable = perfilDisponible && !cargando && !guardando;
        for (View campo : campos.values()) campo.setEnabled(editable);
        btnGuardar.setEnabled(editable);
        btnGuardar.setText(guardando ? R.string.perfil_guardando : R.string.guardar_perfil);
        btnReintentar.setEnabled(!cargando);
    }

    private void mostrarEstado(int recurso) { mostrarEstado(getString(recurso)); }
    private void mostrarEstado(String mensaje) { tvEstado.setText(mensaje); }
    private boolean terminada() { return isFinishing() || isDestroyed(); }
    private String texto(EditText campo) { return campo.getText().toString().trim(); }
    private double decimal(EditText campo) { return Double.parseDouble(texto(campo).replace(',', '.')); }
    private boolean respuestaConPerfil(Response<PerfilResponse> response) {
        return response.isSuccessful() && response.body() != null && response.body().getPerfil() != null;
    }
    private void cerrarError(Response<PerfilResponse> response) {
        if (response.errorBody() != null) response.errorBody().close();
    }

    private void mostrarPerfil(Perfil perfil) {
        etEdad.setText(String.valueOf(perfil.getEdad()));
        etPesoActual.setText(perfil.getPesoActual());
        etAltura.setText(String.valueOf(perfil.getAlturaCm()));
        etPesoObjetivo.setText(perfil.getPesoObjetivo());
        etDiasEntrenamiento.setText(String.valueOf(perfil.getDiasEntrenamiento()));
        etLimitaciones.setText(perfil.getLimitaciones());
        etConsideracionesAlimentarias.setText(perfil.getConsideracionesAlimentarias());
        spSexo.setSelection(posicion(perfil.getSexo(), SEXOS));
        spObjetivo.setSelection(posicion(perfil.getObjetivo(), OBJETIVOS));
        spActividad.setSelection(posicion(perfil.getActividad(), ACTIVIDADES));
        spPreferencia.setSelection(posicion(perfil.getPreferencia(), PREFERENCIAS));
    }

    private static final String[] SEXOS = {"", "m", "f"};
    private static final String[] OBJETIVOS = {"", "bajar_grasa", "aumentar_masa", "mantener_peso", "mejorar_habitos"};
    private static final String[] ACTIVIDADES = {"", "bajo", "moderado", "alto"};
    private static final String[] PREFERENCIAS = {"", "sin_preferencia", "vegetariana", "alta_proteina", "baja_calorias"};
    private String sexo() { return SEXOS[spSexo.getSelectedItemPosition()]; }
    private String objetivo() { return OBJETIVOS[spObjetivo.getSelectedItemPosition()]; }
    private String actividad() { return ACTIVIDADES[spActividad.getSelectedItemPosition()]; }
    private String preferencia() { return PREFERENCIAS[spPreferencia.getSelectedItemPosition()]; }
    private int posicion(String valor, String[] opciones) {
        for (int i = 1; i < opciones.length; i++) if (opciones[i].equals(valor)) return i;
        return 0;
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("perfil_disponible", perfilDisponible);
        outState.putBoolean("guardando", guardando);
        super.onSaveInstanceState(outState);
    }

    @Override protected void onDestroy() {
        if (guardadoCall != null) guardadoCall.cancel();
        super.onDestroy();
    }
}
