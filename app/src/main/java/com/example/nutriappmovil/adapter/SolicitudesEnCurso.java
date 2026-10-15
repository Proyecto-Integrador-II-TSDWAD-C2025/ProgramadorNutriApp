package com.example.nutriappmovil.adapter;

import java.util.HashSet;
import java.util.Set;

/**
 * Registra qué elementos tienen una solicitud en curso, para no enviar dos
 * veces la misma. Hace falta porque completar-comida funciona como alternador:
 * un segundo POST desharía el primero.
 *
 * Se usa solo desde el hilo principal.
 */
public class SolicitudesEnCurso {

    private final Set<Integer> ids = new HashSet<>();

    /**
     * Marca el elemento como "en curso".
     *
     * @return false si ya tenía una solicitud en curso (no hay que enviar otra).
     */
    public boolean iniciar(int id) {
        return ids.add(id);
    }

    public void finalizar(int id) {
        ids.remove(id);
    }

    public boolean contiene(int id) {
        return ids.contains(id);
    }

    public void limpiar() {
        ids.clear();
    }
}
