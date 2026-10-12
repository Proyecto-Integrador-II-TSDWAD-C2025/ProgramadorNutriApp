package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Plan con su menú (PlanDetalleReadSerializer): los mismos campos de Plan
 * más "comidas_plan", que Django ya manda ordenado por día y orden.
 */
public class PlanDetalle extends Plan {

    @SerializedName("comidas_plan")
    private List<PlanComida> comidasPlan;

    public List<PlanComida> getComidasPlan() {
        return comidasPlan;
    }
}
