package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Asignación de un plan alimenticio al usuario (UsuarioPlanDetalleReadSerializer).
 */
public class AsignacionPlan {

    @SerializedName("id_usuario_plan")
    private int idUsuarioPlan;

    // Fechas con formato "2026-10-10"
    @SerializedName("fecha_inicio")
    private String fechaInicio;

    @SerializedName("fecha_fin")
    private String fechaFin;

    // activo | inactivo | completado
    private String estado;

    // orientativo (lo sugiere el sistema) | profesional (lo asigna un nutricionista)
    private String origen;

    // Puede venir vacío
    private String motivo;

    private PlanDetalle plan;

    public int getIdUsuarioPlan() {
        return idUsuarioPlan;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public String getOrigen() {
        return origen;
    }

    public String getMotivo() {
        return motivo;
    }

    public PlanDetalle getPlan() {
        return plan;
    }
}
