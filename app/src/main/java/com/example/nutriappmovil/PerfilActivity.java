package com.example.nutriappmovil;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutriappmovil.model.Perfil;
import com.example.nutriappmovil.model.PerfilRequest;
import com.example.nutriappmovil.model.PerfilResponse;
import com.example.nutriappmovil.network.ApiClient;
import com.example.nutriappmovil.network.PerfilApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {

    private Spinner spSexo;
    private Spinner spObjetivo;
    private Spinner spActividad;
    private Spinner spPreferencia;

    private EditText etEdad;
    private EditText etPesoActual;
    private EditText etAltura;
    private EditText etPesoObjetivo;
    private EditText etDiasEntrenamiento;
    private EditText etLimitaciones;
    private EditText etConsideracionesAlimentarias;

    private Button btnGuardar;
    private Button btnVolver;

    private PerfilApiService perfilApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        inicializarVistas();

        perfilApiService = ApiClient
                .getClient(this)
                .create(PerfilApiService.class);

        btnVolver.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> guardarPerfil());

        cargarPerfil();
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

        etConsideracionesAlimentarias =
                findViewById(R.id.etConsideracionesAlimentarias);

        btnGuardar = findViewById(R.id.btnGuardarPerfil);
        btnVolver = findViewById(R.id.btnVolverPerfil);
    }

    private void cargarPerfil() {

        perfilApiService.getPerfil().enqueue(new Callback<PerfilResponse>() {

            @Override
            public void onResponse(
                    Call<PerfilResponse> call,
                    Response<PerfilResponse> response
            ) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getPerfil() != null) {

                    mostrarPerfil(response.body().getPerfil());
                    return;
                }

                if (response.code() == 404) {
                    // Todavía no existe un perfil.
                    return;
                }

                if (response.code() == 401) {
                    Toast.makeText(
                            PerfilActivity.this,
                            "Necesitás iniciar sesión",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                Toast.makeText(
                        PerfilActivity.this,
                        "No se pudo cargar el perfil",
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onFailure(
                    Call<PerfilResponse> call,
                    Throwable throwable
            ) {

                Toast.makeText(
                        PerfilActivity.this,
                        "Error de conexión",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void guardarPerfil() {

        String edadTexto = etEdad.getText().toString().trim();
        String pesoActualTexto = etPesoActual.getText().toString().trim();
        String alturaTexto = etAltura.getText().toString().trim();
        String pesoObjetivoTexto = etPesoObjetivo.getText().toString().trim();
        String diasTexto = etDiasEntrenamiento.getText().toString().trim();

        if (edadTexto.isEmpty()
                || pesoActualTexto.isEmpty()
                || alturaTexto.isEmpty()
                || pesoObjetivoTexto.isEmpty()
                || diasTexto.isEmpty()) {

            Toast.makeText(
                    this,
                    "Completá todos los campos obligatorios",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (spSexo.getSelectedItemPosition() == 0
                || spObjetivo.getSelectedItemPosition() == 0
                || spActividad.getSelectedItemPosition() == 0
                || spPreferencia.getSelectedItemPosition() == 0) {

            Toast.makeText(
                    this,
                    "Seleccioná todas las opciones",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            int edad = Integer.parseInt(edadTexto);

            double pesoActual = Double.parseDouble(
                    pesoActualTexto.replace(",", ".")
            );

            int altura = Integer.parseInt(alturaTexto);

            double pesoObjetivo = Double.parseDouble(
                    pesoObjetivoTexto.replace(",", ".")
            );

            int diasEntrenamiento = Integer.parseInt(diasTexto);

            if (edad < 13 || edad > 100) {
                etEdad.setError("La edad debe estar entre 13 y 100");
                return;
            }

            if (pesoActual < 30 || pesoActual > 300) {
                etPesoActual.setError(
                        "El peso debe estar entre 30 y 300 kg"
                );
                return;
            }

            if (altura < 100 || altura > 250) {
                etAltura.setError(
                        "La altura debe estar entre 100 y 250 cm"
                );
                return;
            }

            if (pesoObjetivo < 30 || pesoObjetivo > 300) {
                etPesoObjetivo.setError(
                        "El peso debe estar entre 30 y 300 kg"
                );
                return;
            }

            if (diasEntrenamiento < 1 || diasEntrenamiento > 6) {
                etDiasEntrenamiento.setError(
                        "Los días deben estar entre 1 y 6"
                );
                return;
            }

            String sexo = obtenerSexo();
            String objetivo = obtenerObjetivo();
            String actividad = obtenerActividad();
            String preferencia = obtenerPreferencia();

            String limitaciones =
                    etLimitaciones.getText().toString().trim();

            String consideraciones =
                    etConsideracionesAlimentarias
                            .getText()
                            .toString()
                            .trim();

            PerfilRequest request = new PerfilRequest(
                    sexo,
                    edad,
                    pesoActual,
                    altura,
                    pesoObjetivo,
                    objetivo,
                    actividad,
                    preferencia,
                    diasEntrenamiento,
                    limitaciones,
                    consideraciones
            );

            enviarPerfil(request);

        } catch (NumberFormatException exception) {

            Toast.makeText(
                    this,
                    "Revisá los valores numéricos",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void enviarPerfil(PerfilRequest request) {

        btnGuardar.setEnabled(false);

        perfilApiService
                .actualizarPerfil(request)
                .enqueue(new Callback<PerfilResponse>() {

                    @Override
                    public void onResponse(
                            Call<PerfilResponse> call,
                            Response<PerfilResponse> response
                    ) {

                        btnGuardar.setEnabled(true);

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    PerfilActivity.this,
                                    "Perfil guardado correctamente",
                                    Toast.LENGTH_SHORT
                            ).show();

                            if (response.body() != null
                                    && response.body().getPerfil() != null) {

                                mostrarPerfil(
                                        response.body().getPerfil()
                                );
                            }

                            return;
                        }

                        if (response.code() == 400) {

                            Toast.makeText(
                                    PerfilActivity.this,
                                    "Revisá los datos ingresados",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        if (response.code() == 401) {

                            Toast.makeText(
                                    PerfilActivity.this,
                                    "Tu sesión no es válida",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        Toast.makeText(
                                PerfilActivity.this,
                                "No se pudo guardar el perfil",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onFailure(
                            Call<PerfilResponse> call,
                            Throwable throwable
                    ) {

                        btnGuardar.setEnabled(true);

                        Toast.makeText(
                                PerfilActivity.this,
                                "Error de conexión",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void mostrarPerfil(Perfil perfil) {

        etEdad.setText(String.valueOf(perfil.getEdad()));
        etPesoActual.setText(perfil.getPesoActual());
        etAltura.setText(String.valueOf(perfil.getAlturaCm()));
        etPesoObjetivo.setText(perfil.getPesoObjetivo());

        etDiasEntrenamiento.setText(
                String.valueOf(perfil.getDiasEntrenamiento())
        );

        etLimitaciones.setText(perfil.getLimitaciones());

        etConsideracionesAlimentarias.setText(
                perfil.getConsideracionesAlimentarias()
        );

        spSexo.setSelection(
                posicionSexo(perfil.getSexo())
        );

        spObjetivo.setSelection(
                posicionObjetivo(perfil.getObjetivo())
        );

        spActividad.setSelection(
                posicionActividad(perfil.getActividad())
        );

        spPreferencia.setSelection(
                posicionPreferencia(perfil.getPreferencia())
        );
    }

    private String obtenerSexo() {

        switch (spSexo.getSelectedItemPosition()) {
            case 1:
                return "m";

            case 2:
                return "f";

            default:
                return "";
        }
    }

    private String obtenerObjetivo() {

        switch (spObjetivo.getSelectedItemPosition()) {
            case 1:
                return "bajar_grasa";

            case 2:
                return "aumentar_masa";

            case 3:
                return "mantener_peso";

            case 4:
                return "mejorar_habitos";

            default:
                return "";
        }
    }

    private String obtenerActividad() {

        switch (spActividad.getSelectedItemPosition()) {
            case 1:
                return "bajo";

            case 2:
                return "moderado";

            case 3:
                return "alto";

            default:
                return "";
        }
    }

    private String obtenerPreferencia() {

        switch (spPreferencia.getSelectedItemPosition()) {
            case 1:
                return "sin_preferencia";

            case 2:
                return "vegetariana";

            case 3:
                return "alta_proteina";

            case 4:
                return "baja_calorias";

            default:
                return "";
        }
    }

    private int posicionSexo(String sexo) {

        if ("m".equals(sexo)) {
            return 1;
        }

        if ("f".equals(sexo)) {
            return 2;
        }

        return 0;
    }

    private int posicionObjetivo(String objetivo) {

        if (objetivo == null) {
            return 0;
        }

        switch (objetivo) {
            case "bajar_grasa":
                return 1;

            case "aumentar_masa":
                return 2;

            case "mantener_peso":
                return 3;

            case "mejorar_habitos":
                return 4;

            default:
                return 0;
        }
    }

    private int posicionActividad(String actividad) {

        if (actividad == null) {
            return 0;
        }

        switch (actividad) {
            case "bajo":
                return 1;

            case "moderado":
                return 2;

            case "alto":
                return 3;

            default:
                return 0;
        }
    }

    private int posicionPreferencia(String preferencia) {

        if (preferencia == null) {
            return 0;
        }

        switch (preferencia) {
            case "sin_preferencia":
                return 1;

            case "vegetariana":
                return 2;

            case "alta_proteina":
                return 3;

            case "baja_calorias":
                return 4;

            default:
                return 0;
        }
    }
}