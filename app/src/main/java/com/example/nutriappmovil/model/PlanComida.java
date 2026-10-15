package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Comida ubicada dentro de un plan (PlanComidaReadSerializer): qué se come,
 * en qué día del ciclo y en qué orden.
 *
 * "completada_hoy" lo calcula Django por usuario y por fecha del servidor,
 * así que vuelve a false al día siguiente.
 */
public class PlanComida {

    @SerializedName("id_plan_comida")
    private int idPlanComida;

    @SerializedName("id_plan")
    private int idPlan;

    // En el JSON se llama "id_comida" pero trae el objeto completo
    @SerializedName("id_comida")
    private Comida comida;

    // Día del ciclo: 1 a 7
    private int dia;

    private int orden;

    // desayuno | almuerzo | merienda | cena | snack
    @SerializedName("tipo_comida")
    private String tipoComida;

    private String porcion;

    // Puede venir vacío
    private String alternativa;

    @SerializedName("completada_hoy")
    private boolean completadaHoy;

    public int getIdPlanComida() {
        return idPlanComida;
    }

    public int getIdPlan() {
        return idPlan;
    }

    public Comida getComida() {
        return comida;
    }

    public int getDia() {
        return dia;
    }

    public int getOrden() {
        return orden;
    }

    public String getTipoComida() {
        return tipoComida;
    }

    public String getPorcion() {
        return porcion;
    }

    public String getAlternativa() {
        return alternativa;
    }

    public boolean isCompletadaHoy() {
        return completadaHoy;
    }

    // La pantalla lo actualiza con la respuesta de completar-comida
    public void setCompletadaHoy(boolean completadaHoy) {
        this.completadaHoy = completadaHoy;
    }
}
