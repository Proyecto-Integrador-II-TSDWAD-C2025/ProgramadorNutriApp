package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Respuesta de POST /api/mi-plan-alimenticio/completar-comida/.
 * "completada_hoy" es el estado en que quedó la comida después de la llamada.
 */
public class CompletarComidaResponse {

    @SerializedName("plan_comida_id")
    private int planComidaId;

    @SerializedName("completada_hoy")
    private boolean completadaHoy;

    public int getPlanComidaId() {
        return planComidaId;
    }

    public boolean isCompletadaHoy() {
        return completadaHoy;
    }
}
