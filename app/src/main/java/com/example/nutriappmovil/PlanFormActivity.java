package com.example.nutriappmovil;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.nutriappmovil.model.Plan;
import com.example.nutriappmovil.model.PlanRequest;
import com.example.nutriappmovil.network.ApiClient;
import com.example.nutriappmovil.network.PlanesApiService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Alta y edición de planes (POST / PUT a /api/planes/).
 *
 * - Sin extra: alta. Con EXTRA_PLAN_JSON: edición con los datos precargados.
 * - La app valida primero (duración 1-365, calorías 500-6000) y Django vuelve a
 *   validar: si responde 400, cada mensaje se muestra debajo de su campo.
 * - 401 vuelve al Login, 403 es "sin permiso" y 404 es "el plan ya no existe".
 * - El botón Guardar se deshabilita mientras se envía, para no duplicar pedidos.
 * Es hija de PlanesActivity.
 */
public class PlanFormActivity extends AppCompatActivity {

    public static final String EXTRA_PLAN_JSON = "plan_json";

    // Campos de Django que tienen su propio mensaje de error en pantalla
    private static final List<String> CAMPOS_CON_ERROR_PROPIO = Arrays.asList(
            "nombre_plan", "descripcion", "duracion_dias", "calorias_objetivo", "observaciones");

    private PlanesApiService planesService;
    private Call<Plan> llamadaGuardar;

    private Plan planEditando;
    private boolean esEdicion;
    private boolean guardando = false;

    private EditText etNombre;
    private EditText etDescripcion;
    private EditText etDuracion;
    private EditText etCalorias;
    private EditText etObservaciones;
    private Spinner spObjetivo;
    private Spinner spNivelActividad;
    private Spinner spPreferencia;
    private SwitchCompat switchActivo;

    private TextView tvErrorNombre;
    private TextView tvErrorDescripcion;
    private TextView tvErrorDuracion;
    private TextView tvErrorCalorias;
    private TextView tvErrorObservaciones;
    private TextView tvErrorGeneral;

    private Button btnGuardar;
    private Button btnCancelar;

    // Primer campo con error, para llevarle el foco
    private View primerCampoConError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_form);

        planesService = ApiClient.getClient(this).create(PlanesApiService.class);

        String planJson = getIntent().getStringExtra(EXTRA_PLAN_JSON);
        if (planJson != null) {
            planEditando = new Gson().fromJson(planJson, Plan.class);
            if (planEditando == null) {
                finish();
                return;
            }
            esEdicion = true;
        }

        buscarVistas();
        configurarSpinners();

        ((TextView) findViewById(R.id.tvTituloForm)).setText(esEdicion ? "Editar plan" : "Nuevo plan");

        if (esEdicion) {
            precargarDatos();
        } else {
            // Mismos valores por defecto que usa Django
            spObjetivo.setSelection(PlanOpciones.indice(this, R.array.plan_objetivo_values, "mantener_peso"));
            spNivelActividad.setSelection(
                    PlanOpciones.indice(this, R.array.plan_nivel_actividad_values, "moderado"));
            spPreferencia.setSelection(PlanOpciones.indice(this, R.array.plan_preferencia_values, "todas"));
            switchActivo.setChecked(true);
        }

        findViewById(R.id.btnVolverForm).setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardar());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (llamadaGuardar != null) {
            llamadaGuardar.cancel();
        }
    }

    private void buscarVistas() {
        etNombre = findViewById(R.id.etNombrePlan);
        etDescripcion = findViewById(R.id.etDescripcionPlan);
        etDuracion = findViewById(R.id.etDuracionPlan);
        etCalorias = findViewById(R.id.etCaloriasPlan);
        etObservaciones = findViewById(R.id.etObservacionesPlan);
        spObjetivo = findViewById(R.id.spObjetivo);
        spNivelActividad = findViewById(R.id.spNivelActividad);
        spPreferencia = findViewById(R.id.spPreferencia);
        switchActivo = findViewById(R.id.switchActivoPlan);

        tvErrorNombre = findViewById(R.id.tvErrorNombrePlan);
        tvErrorDescripcion = findViewById(R.id.tvErrorDescripcionPlan);
        tvErrorDuracion = findViewById(R.id.tvErrorDuracionPlan);
        tvErrorCalorias = findViewById(R.id.tvErrorCaloriasPlan);
        tvErrorObservaciones = findViewById(R.id.tvErrorObservacionesPlan);
        tvErrorGeneral = findViewById(R.id.tvErrorGeneralPlan);

        btnGuardar = findViewById(R.id.btnGuardarPlan);
        btnCancelar = findViewById(R.id.btnCancelarPlan);
    }

    private void configurarSpinners() {
        asignarOpciones(spObjetivo, R.array.plan_objetivo_labels);
        asignarOpciones(spNivelActividad, R.array.plan_nivel_actividad_labels);
        asignarOpciones(spPreferencia, R.array.plan_preferencia_labels);
    }

    private void asignarOpciones(Spinner spinner, int labelsRes) {
        ArrayAdapter<CharSequence> opciones = ArrayAdapter.createFromResource(
                this, labelsRes, android.R.layout.simple_spinner_item);
        opciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(opciones);
    }

    private void precargarDatos() {
        etNombre.setText(planEditando.getNombrePlan());
        etDescripcion.setText(planEditando.getDescripcion());
        etDuracion.setText(String.valueOf(planEditando.getDuracionDias()));
        etCalorias.setText(PlanOpciones.formatearNumero(planEditando.getCaloriasObjetivo()));
        etObservaciones.setText(planEditando.getObservaciones());

        spObjetivo.setSelection(
                PlanOpciones.indice(this, R.array.plan_objetivo_values, planEditando.getObjetivo()));
        spNivelActividad.setSelection(
                PlanOpciones.indice(this, R.array.plan_nivel_actividad_values, planEditando.getNivelActividad()));
        spPreferencia.setSelection(
                PlanOpciones.indice(this, R.array.plan_preferencia_values, planEditando.getPreferenciaCompatible()));
        switchActivo.setChecked(planEditando.isActivo());
    }

    // ---------- Guardar ----------

    private void guardar() {
        if (guardando) {
            return;
        }

        PlanRequest request = validarYArmarRequest();
        if (request == null) {
            enfocarPrimerError();
            return;
        }

        setGuardando(true);

        llamadaGuardar = esEdicion
                ? planesService.actualizarPlan(planEditando.getIdPlan(), request)
                : planesService.crearPlan(request);

        llamadaGuardar.enqueue(new Callback<Plan>() {
            @Override
            public void onResponse(Call<Plan> call, Response<Plan> response) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                setGuardando(false);

                if (response.isSuccessful()) {
                    Toast.makeText(PlanFormActivity.this,
                            esEdicion ? "Plan actualizado." : "Plan creado.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else if (response.code() == 400) {
                    mostrarErroresDeDjango(response.errorBody());
                } else if (response.code() == 401) {
                    SesionUtil.volverAlLoginPorSesionExpirada(PlanFormActivity.this);
                } else if (response.code() == 403) {
                    mostrarErrorGeneral("No tenés permiso para guardar planes.");
                } else if (response.code() == 404) {
                    mostrarErrorGeneral("Ese plan ya no existe. Volvé al listado y actualizalo.");
                } else {
                    mostrarErrorGeneral("No se pudo guardar el plan (código " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<Plan> call, Throwable t) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) {
                    return;
                }
                setGuardando(false);
                mostrarErrorGeneral("No hay conexión con el servidor. Revisá tu internet e intentá de nuevo.");
            }
        });
    }

    private void setGuardando(boolean valor) {
        guardando = valor;
        btnGuardar.setEnabled(!valor);
        btnCancelar.setEnabled(!valor);
        btnGuardar.setText(valor ? "Guardando…" : "Guardar");
    }

    // ---------- Validación local ----------

    /** Devuelve el pedido listo para enviar, o null si hay errores (ya mostrados en pantalla). */
    private PlanRequest validarYArmarRequest() {
        limpiarErrores();

        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String duracionTexto = etDuracion.getText().toString().trim();
        String caloriasTexto = etCalorias.getText().toString().trim().replace(',', '.');
        String observaciones = etObservaciones.getText().toString().trim();

        if (nombre.isEmpty()) {
            mostrarErrorCampo(tvErrorNombre, etNombre, "Ingresá el nombre del plan.");
        } else if (nombre.length() > 100) {
            mostrarErrorCampo(tvErrorNombre, etNombre, "El nombre no puede superar los 100 caracteres.");
        }

        if (descripcion.isEmpty()) {
            mostrarErrorCampo(tvErrorDescripcion, etDescripcion, "Ingresá una descripción.");
        }

        int duracion = 0;
        if (duracionTexto.isEmpty()) {
            mostrarErrorCampo(tvErrorDuracion, etDuracion, "Ingresá la duración en días.");
        } else {
            try {
                duracion = Integer.parseInt(duracionTexto);
                if (duracion < 1 || duracion > 365) {
                    mostrarErrorCampo(tvErrorDuracion, etDuracion,
                            "La duración debe estar entre 1 y 365 días.");
                }
            } catch (NumberFormatException e) {
                mostrarErrorCampo(tvErrorDuracion, etDuracion, "Ingresá un número entero de días.");
            }
        }

        double calorias = 0;
        if (caloriasTexto.isEmpty()) {
            mostrarErrorCampo(tvErrorCalorias, etCalorias, "Ingresá las calorías objetivo.");
        } else {
            try {
                BigDecimal valor = new BigDecimal(caloriasTexto);
                calorias = valor.doubleValue();
                if (calorias < 500 || calorias > 6000) {
                    mostrarErrorCampo(tvErrorCalorias, etCalorias,
                            "Las calorías deben estar entre 500 y 6000 kcal.");
                } else if (valor.stripTrailingZeros().scale() > 2) {
                    mostrarErrorCampo(tvErrorCalorias, etCalorias, "Usá como máximo 2 decimales.");
                }
            } catch (NumberFormatException e) {
                mostrarErrorCampo(tvErrorCalorias, etCalorias, "Ingresá un número válido de calorías.");
            }
        }

        if (primerCampoConError != null) {
            return null;
        }

        return new PlanRequest(
                nombre,
                descripcion,
                duracion,
                calorias,
                PlanOpciones.valor(this, R.array.plan_objetivo_values, spObjetivo.getSelectedItemPosition()),
                PlanOpciones.valor(this, R.array.plan_nivel_actividad_values,
                        spNivelActividad.getSelectedItemPosition()),
                PlanOpciones.valor(this, R.array.plan_preferencia_values, spPreferencia.getSelectedItemPosition()),
                observaciones,
                switchActivo.isChecked());
    }

    // ---------- Errores de Django (400) ----------

    /** Django responde {"campo": ["mensaje"]}: cada mensaje va debajo de su campo. */
    private void mostrarErroresDeDjango(ResponseBody errorBody) {
        limpiarErrores();

        try {
            String cuerpo = errorBody != null ? errorBody.string() : "";
            JsonObject json = new JsonParser().parse(cuerpo).getAsJsonObject();

            aplicarErrorDeCampo(json, "nombre_plan", tvErrorNombre, etNombre);
            aplicarErrorDeCampo(json, "descripcion", tvErrorDescripcion, etDescripcion);
            aplicarErrorDeCampo(json, "duracion_dias", tvErrorDuracion, etDuracion);
            aplicarErrorDeCampo(json, "calorias_objetivo", tvErrorCalorias, etCalorias);
            aplicarErrorDeCampo(json, "observaciones", tvErrorObservaciones, etObservaciones);

            // Cualquier otro error (no_field_errors, objetivo, etc.) va como mensaje general
            StringBuilder generales = new StringBuilder();
            for (Map.Entry<String, JsonElement> entrada : json.entrySet()) {
                String clave = entrada.getKey();
                if (CAMPOS_CON_ERROR_PROPIO.contains(clave)) {
                    continue;
                }
                if (generales.length() > 0) {
                    generales.append("\n");
                }
                if ("detail".equals(clave) || "non_field_errors".equals(clave)) {
                    generales.append(primerMensaje(entrada.getValue()));
                } else {
                    generales.append(clave).append(": ").append(primerMensaje(entrada.getValue()));
                }
            }

            if (generales.length() > 0) {
                mostrarErrorGeneral(generales.toString());
            } else if (primerCampoConError == null) {
                mostrarErrorGeneral("Revisá los datos ingresados.");
            }
            enfocarPrimerError();
        } catch (Exception e) {
            mostrarErrorGeneral("Revisá los datos ingresados.");
        }
    }

    private void aplicarErrorDeCampo(JsonObject json, String clave, TextView tvError, View campo) {
        if (json.has(clave)) {
            mostrarErrorCampo(tvError, campo, primerMensaje(json.get(clave)));
        }
    }

    private String primerMensaje(JsonElement elemento) {
        if (elemento.isJsonArray() && elemento.getAsJsonArray().size() > 0) {
            JsonElement primero = elemento.getAsJsonArray().get(0);
            return primero.isJsonPrimitive() ? primero.getAsString() : primero.toString();
        }
        return elemento.isJsonPrimitive() ? elemento.getAsString() : elemento.toString();
    }

    // ---------- Mostrar y limpiar errores ----------

    private void mostrarErrorCampo(TextView tvError, View campo, String mensaje) {
        tvError.setText(mensaje);
        tvError.setVisibility(View.VISIBLE);
        campo.setBackgroundResource(R.drawable.plan_input_error);
        if (primerCampoConError == null) {
            primerCampoConError = campo;
        }
    }

    private void mostrarErrorGeneral(String mensaje) {
        tvErrorGeneral.setText(mensaje);
        tvErrorGeneral.setVisibility(View.VISIBLE);
    }

    private void limpiarErrores() {
        primerCampoConError = null;

        TextView[] errores = {tvErrorNombre, tvErrorDescripcion, tvErrorDuracion,
                tvErrorCalorias, tvErrorObservaciones, tvErrorGeneral};
        for (TextView error : errores) {
            error.setText("");
            error.setVisibility(View.GONE);
        }

        EditText[] campos = {etNombre, etDescripcion, etDuracion, etCalorias, etObservaciones};
        for (EditText campo : campos) {
            campo.setBackgroundResource(R.drawable.plan_input);
        }
    }

    private void enfocarPrimerError() {
        if (primerCampoConError != null) {
            primerCampoConError.requestFocus();
        }
    }
}