package com.example.nutriappmovil.perfil;

/** Validaciones numericas compatibles con los rangos y decimales de la API. */
public final class ValidacionPerfil {
    private ValidacionPerfil() { }

    public static boolean enteroEnRango(String texto, int minimo, int maximo) {
        if (texto == null || !texto.trim().matches("[0-9]+")) {
            return false;
        }
        try {
            int valor = Integer.parseInt(texto.trim());
            return valor >= minimo && valor <= maximo;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean decimalEnRango(String texto, double minimo, double maximo) {
        if (texto == null || !texto.trim().matches("[0-9]+(?:[.,][0-9]{0,2})?")) {
            return false;
        }
        try {
            double valor = Double.parseDouble(texto.trim().replace(',', '.'));
            return !Double.isNaN(valor) && !Double.isInfinite(valor)
                    && valor >= minimo && valor <= maximo;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
