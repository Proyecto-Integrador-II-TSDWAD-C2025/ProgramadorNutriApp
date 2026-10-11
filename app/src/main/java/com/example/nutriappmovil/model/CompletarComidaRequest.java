package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Cuerpo de POST /api/mi-plan-alimenticio/completar-comida/.
 * Mismo estilo que PerfilRequest: campo private final + constructor.
 */
public class CompletarComidaRequest {

    @SerializedName("plan_comida_id")
    private final int planComidaId;

    public CompletarComidaRequest(int planComidaId) {
        this.planComidaId = planComidaId;
    }
}
