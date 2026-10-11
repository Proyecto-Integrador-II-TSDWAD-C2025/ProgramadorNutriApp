package com.example.nutriappmovil.adapter;

import com.example.nutriappmovil.model.PlanComida;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Lógica de presentación del menú, sin dependencias de Android para poder
 * probarla con tests unitarios.
 */
public final class MenuPlanAlimenticio {

    private static final Locale LOCALE_AR = new Locale("es", "AR");

    private MenuPlanAlimenticio() {
    }

    /**
     * Ordena las comidas por día y orden, y arma una lista plana donde se
     * intercalan encabezados (Integer con el número de día) y comidas
     * (PlanComida) para el RecyclerView.
     */
    public static List<Object> agruparPorDia(List<PlanComida> comidas) {
        List<Object> items = new ArrayList<>();
        if (comidas == null) {
            return items;
        }

        List<PlanComida> ordenadas = new ArrayList<>();
        for (PlanComida comida : comidas) {
            if (comida != null) {
                ordenadas.add(comida);
            }
        }

        Collections.sort(ordenadas, new Comparator<PlanComida>() {
            @Override
            public int compare(PlanComida a, PlanComida b) {
                if (a.getDia() != b.getDia()) {
                    return Integer.compare(a.getDia(), b.getDia());
                }
                if (a.getOrden() != b.getOrden()) {
                    return Integer.compare(a.getOrden(), b.getOrden());
                }
                return Integer.compare(a.getIdPlanComida(), b.getIdPlanComida());
            }
        });

        Integer diaActual = null;
        for (PlanComida comida : ordenadas) {
            if (diaActual == null || diaActual != comida.getDia()) {
                diaActual = comida.getDia();
                items.add(diaActual);
            }
            items.add(comida);
        }

        return items;
    }

    /** 340.0 -> "340"; 12.5 -> "12,5". Como máximo un decimal. */
    public static String formatearCantidad(double valor) {
        return new DecimalFormat("0.#", new DecimalFormatSymbols(LOCALE_AR)).format(valor);
    }

    /** "2026-10-10" -> "10/10/2026". Si no tiene ese formato, lo devuelve como llegó. */
    public static String formatearFecha(String fechaIso) {
        if (fechaIso == null) {
            return "";
        }

        String[] partes = fechaIso.trim().split("-");
        if (partes.length != 3) {
            return fechaIso;
        }

        return partes[2] + "/" + partes[1] + "/" + partes[0];
    }
}
