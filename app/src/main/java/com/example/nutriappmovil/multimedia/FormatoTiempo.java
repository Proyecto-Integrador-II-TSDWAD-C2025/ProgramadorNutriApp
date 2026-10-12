package com.example.nutriappmovil.multimedia;

import java.util.Locale;

/**
 * Formato del tiempo de reproducción. Sin dependencias de Android para poder
 * probarlo con tests unitarios.
 */
public final class FormatoTiempo {

    private FormatoTiempo() {
    }

    /** 5000 -> "0:05"; 65000 -> "1:05". Un valor negativo se toma como cero. */
    public static String minutosYSegundos(int milisegundos) {
        int totalSegundos = Math.max(0, milisegundos) / 1000;
        return String.format(Locale.ROOT, "%d:%02d", totalSegundos / 60, totalSegundos % 60);
    }

    /** Porcentaje de avance entre 0 y 100. Sin duración conocida devuelve 0. */
    public static int porcentaje(int posicionMs, int duracionMs) {
        if (duracionMs <= 0) {
            return 0;
        }

        long porcentaje = Math.max(0, posicionMs) * 100L / duracionMs;
        return (int) Math.min(100, porcentaje);
    }
}
