package com.example.nutriappmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutriappmovil.adapter.EjercicioAdapter;
import com.example.nutriappmovil.data.MockMiRutinaData;
import com.example.nutriappmovil.model.Asignacion;
import com.example.nutriappmovil.model.Ejercicio;
import com.example.nutriappmovil.model.MiRutinaResponse;
import com.example.nutriappmovil.model.Rutina;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity "Mi Rutina".
 * Hija de DashboardActivity. Muestra la rutina asignada al usuario.
 *
 * TODO Sprint futuro: reemplazar MockMiRutinaData.getRutinaCompleta() por la
 * llamada al endpoint GET /api/mi-rutina/ y deserializar directamente en
 * MiRutinaResponse usando Gson.
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_rutina);

        initViews();
        setupRecyclerView();
        setupListeners();

        // Cargar datos mock. En un sprint futuro se reemplaza por la llamada a la API.
        MiRutinaResponse response = MockMiRutinaData.getRutinaCompleta();
        // Para probar otros estados, descomentar una de las siguientes lineas:
        // MiRutinaResponse response = MockMiRutinaData.getSinAsignacion();
        // MiRutinaResponse response = MockMiRutinaData.getEnRevision();

        renderizarEstado(response);
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
    }

    private void setupRecyclerView() {
        recyclerEjercicios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EjercicioAdapter();
        recyclerEjercicios.setAdapter(adapter);
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(v -> {
            // Volver al DashboardActivity cuando este disponible.
            // Por ahora, finish() devuelve al activity anterior en el stack.
            // TODO: cuando DashboardActivity este mergeado, se puede usar:
            // Intent intent = new Intent(this, DashboardActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            // startActivity(intent);
            finish();
        });
    }

    private void renderizarEstado(MiRutinaResponse response) {
        if (response == null) {
            mostrarVacio();
            return;
        }

        // Banner de revision
        if (response.isRequiereRevision()) {
            bannerRevision.setVisibility(View.VISIBLE);
            tvBannerRevision.setText("Tu rutina esta en revision");
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
