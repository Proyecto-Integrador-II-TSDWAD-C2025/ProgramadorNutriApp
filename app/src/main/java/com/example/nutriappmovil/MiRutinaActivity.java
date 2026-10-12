package com.example.nutriappmovil;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutriappmovil.adapter.EjercicioAdapter;
import com.example.nutriappmovil.model.Asignacion;
import com.example.nutriappmovil.model.CompletarEjercicioRequest;
import com.example.nutriappmovil.model.CompletarEjercicioResponse;
import com.example.nutriappmovil.model.Ejercicio;
import com.example.nutriappmovil.model.MiRutinaResponse;
import com.example.nutriappmovil.model.Rutina;
import com.example.nutriappmovil.network.ApiClient;
import com.example.nutriappmovil.network.RutinaApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity "Mi Rutina".
 * Conecta con el backend via RutinaApiService.
 * Maneja estados: carga, sin asignacion, revision, error.
 * Optimistic UI al completar ejercicios con rollback si falla la API.
 */
public class MiRutinaActivity extends AppCompatActivity {

    private View bannerRevision;
    private TextView tvBannerRevision;
    private View layoutInfoRutina;
    private View layoutEmpty;
    private TextView tvNombreRutina;
    private TextView tvDescripcionRutina;
    private TextView tagNivel;
    private TextView tagObjetivo;
    private TextView tagDuracion;
    private RecyclerView recyclerEjercicios;
    private EjercicioAdapter adapter;
    private Button btnVolver;
    private View layoutLoading;

    private RutinaApiService rutinaApiService;
    private final Set<Integer> completandoIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_rutina);

        rutinaApiService = ApiClient.getClient(this).create(RutinaApiService.class);

        initViews();
        setupRecyclerView();
        setupListeners();
        setupAccessibilityIds();

        cargarRutina();
    }

    private void initViews() {
        bannerRevision = findViewById(R.id.bannerRevision);
        tvBannerRevision = findViewById(R.id.tvBannerRevision);
        layoutInfoRutina = findViewById(R.id.layoutInfoRutina);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvNombreRutina = findViewById(R.id.tvNombreRutina);
        tvDescripcionRutina = findViewById(R.id.tvDescripcionRutina);
        tagNivel = findViewById(R.id.tagNivel);
        tagObjetivo = findViewById(R.id.tagObjetivo);
        tagDuracion = findViewById(R.id.tagDuracion);
        recyclerEjercicios = findViewById(R.id.recyclerEjercicios);
        btnVolver = findViewById(R.id.btnVolver);
        layoutLoading = findViewById(R.id.layoutLoading);
    }

    private void setupRecyclerView() {
        recyclerEjercicios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EjercicioAdapter();
        adapter.setOnEjercicioClickListener((ejercicio, position) -> {
            onEjercicioClicked(ejercicio, position);
        });
        recyclerEjercicios.setAdapter(adapter);
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(v -> finish());
    }

    private void setupAccessibilityIds() {
        bannerRevision.setContentDescription("bannerRevision");
        layoutEmpty.setContentDescription("layoutEmpty");
        btnVolver.setContentDescription("btnVolver");
    }

    private void cargarRutina() {
        mostrarCargando(true);

        rutinaApiService.getMiRutina().enqueue(new Callback<MiRutinaResponse>() {
            @Override
            public void onResponse(Call<MiRutinaResponse> call, Response<MiRutinaResponse> response) {
                mostrarCargando(false);
                if (response.isSuccessful() && response.body() != null) {
                    renderizarEstado(response.body());
                } else {
                    mostrarError("No se pudo cargar tu rutina. Intenta de nuevo.");
                }
            }

            @Override
            public void onFailure(Call<MiRutinaResponse> call, Throwable t) {
                mostrarCargando(false);
                mostrarError("Error de conexion. Verifica tu red e intenta de nuevo.");
            }
        });
    }

    private void mostrarCargando(boolean cargando) {
        if (layoutLoading != null) {
            layoutLoading.setVisibility(cargando ? View.VISIBLE : View.GONE);
        }
        if (!cargando) {
            // Nada; el estado se decide en renderizarEstado
        } else {
            layoutInfoRutina.setVisibility(View.GONE);
            recyclerEjercicios.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
            bannerRevision.setVisibility(View.GONE);
        }
    }

    private void renderizarEstado(MiRutinaResponse response) {
        if (response == null) {
            mostrarVacio();
            return;
        }

        // Banner de revision
        if (response.isRequiereRevision()) {
            bannerRevision.setVisibility(View.VISIBLE);
            tvBannerRevision.setText(response.getMensaje() != null
                    ? response.getMensaje()
                    : "Tu rutina esta en revision");
        } else {
            bannerRevision.setVisibility(View.GONE);
        }

        Asignacion asignacion = response.getAsignacion();
        if (asignacion == null || asignacion.getRutina() == null) {
            mostrarVacio();
            return;
        }

        mostrarRutina(asignacion.getRutina());
    }

    private void mostrarVacio() {
        layoutInfoRutina.setVisibility(View.GONE);
        recyclerEjercicios.setVisibility(View.GONE);
        bannerRevision.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void mostrarRutina(Rutina rutina) {
        layoutEmpty.setVisibility(View.GONE);
        layoutInfoRutina.setVisibility(View.VISIBLE);
        recyclerEjercicios.setVisibility(View.VISIBLE);

        tvNombreRutina.setText(rutina.getNombre());
        tvDescripcionRutina.setText(rutina.getDescripcion());
        tagNivel.setText(capitalize(rutina.getNivel()));
        tagObjetivo.setText(formatearObjetivo(rutina.getObjetivo()));
        tagDuracion.setText(rutina.getDuracionSemanas() + " semanas");

        List<Object> itemsPlanos = construirListaPlana(rutina.getEjercicios());
        adapter.setItems(itemsPlanos);
    }

    private void onEjercicioClicked(Ejercicio ejercicio, int position) {
        if (completandoIds.contains(ejercicio.getIdEjercicio())) {
            return; // Bloquear doble toque
        }

        completandoIds.add(ejercicio.getIdEjercicio());

        boolean estadoPrevio = ejercicio.isCompletadoHoy();
        ejercicio.setCompletadoHoy(!estadoPrevio);
        adapter.updateItem(position, ejercicio);

        rutinaApiService.completarEjercicio(
                new CompletarEjercicioRequest(ejercicio.getIdEjercicio())
        ).enqueue(new Callback<CompletarEjercicioResponse>() {
            @Override
            public void onResponse(Call<CompletarEjercicioResponse> call,
                                   Response<CompletarEjercicioResponse> response) {
                completandoIds.remove(ejercicio.getIdEjercicio());
                if (response.isSuccessful() && response.body() != null) {
                    // Confirmado por el backend. Sincronizar estado exacto.
                    ejercicio.setCompletadoHoy(response.body().isCompletadoHoy());
                    adapter.updateItem(position, ejercicio);
                } else {
                    // Error del servidor: revertir
                    revertirEstado(ejercicio, position, estadoPrevio);
                    mostrarError("No se pudo guardar el ejercicio. Intenta de nuevo.");
                }
            }

            @Override
            public void onFailure(Call<CompletarEjercicioResponse> call, Throwable t) {
                completandoIds.remove(ejercicio.getIdEjercicio());
                revertirEstado(ejercicio, position, estadoPrevio);
                mostrarError("Error de conexion. Se revirtio el cambio.");
            }
        });
    }

    private void revertirEstado(Ejercicio ejercicio, int position, boolean estadoPrevio) {
        ejercicio.setCompletadoHoy(estadoPrevio);
        adapter.updateItem(position, ejercicio);
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    /**
     * Agrupa los ejercicios por dia y arma una lista plana donde se intercalan
     * headers (String) y ejercicios (Ejercicio) para el RecyclerView.
     */
    private List<Object> construirListaPlana(List<Ejercicio> ejercicios) {
        List<Object> plana = new ArrayList<>();
        if (ejercicios == null || ejercicios.isEmpty()) {
            return plana;
        }

        // Ordenar por dia y luego por orden
        List<Ejercicio> ordenados = new ArrayList<>(ejercicios);
        Collections.sort(ordenados, new Comparator<Ejercicio>() {
            @Override
            public int compare(Ejercicio e1, Ejercicio e2) {
                if (e1.getDia() != e2.getDia()) {
                    return Integer.compare(e1.getDia(), e2.getDia());
                }
                return Integer.compare(e1.getOrden(), e2.getOrden());
            }
        });

        // Agrupar por dia manteniendo orden
        Map<Integer, List<Ejercicio>> porDia = new LinkedHashMap<>();
        for (Ejercicio e : ordenados) {
            if (!porDia.containsKey(e.getDia())) {
                porDia.put(e.getDia(), new ArrayList<>());
            }
            porDia.get(e.getDia()).add(e);
        }

        for (Map.Entry<Integer, List<Ejercicio>> entry : porDia.entrySet()) {
            plana.add("Dia " + entry.getKey());
            plana.addAll(entry.getValue());
        }

        return plana;
    }

    private String capitalize(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    private String formatearObjetivo(String objetivo) {
        if (objetivo == null) return "";
        switch (objetivo) {
            case "aumentar_masa":
                return "Aumentar masa";
            case "perder_grasa":
                return "Perder grasa";
            case "mantener":
                return "Mantener";
            default:
                return capitalize(objetivo.replace("_", " "));
        }
    }
}
