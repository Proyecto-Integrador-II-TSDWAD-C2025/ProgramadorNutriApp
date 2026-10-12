package com.example.nutriappmovil;

import android.content.Context;

import java.util.Locale;

/**
 * Utilidades para las opciones fijas de los planes (res/values/plan_options.xml)
 * y para mostrar números sin ceros de más.
 */
public final class PlanOpciones {

    private PlanOpciones() {
    }

    /** Texto para mostrar de un valor de Django: "bajar_grasa" -> "Bajar grasa corporal". */
    public static String etiqueta(Context context, int valuesRes, int labelsRes, String valor) {
        String[] values = context.getResources().getStringArray(valuesRes);
        String[] labels = context.getResources().getStringArray(labelsRes);

        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(valor)) {
                return labels[i];
            }
        }
        return valor == null ? "-" : valor;
    }

    /** Posición de un valor dentro del array de values (sirve para preseleccionar un Spinner). */
    public static int indice(Context context, int valuesRes, String valor) {
        String[] values = context.getResources().getStringArray(valuesRes);

        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(valor)) {
                return i;
            }
        }
        return 0;
    }

    /** Valor que acepta Django para la posición elegida en un Spinner. */
    public static String valor(Context context, int valuesRes, int posicion) {
        String[] values = context.getResources().getStringArray(valuesRes);

        if (posicion < 0 || posicion >= values.length) {
            return values[0];
        }
        return values[posicion];
    }

    /** 2000.0 -> "2000" ; 1999.5 -> "1999.5" */
    public static String formatearNumero(double numero) {
        if (numero == Math.floor(numero)) {
            return String.format(Locale.US, "%.0f", numero);
        }
        return String.valueOf(numero);
    }
}