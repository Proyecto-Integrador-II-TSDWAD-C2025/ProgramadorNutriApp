package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Respuesta de GET /api/mi-plan-alimenticio/ (MiPlanAlimenticioView).
 *
 * Si "requiere_revision" es true, "asignacion" viene en null y "mensaje"
 * explica el motivo. Con plan asignado, "mensaje" llega vacío.
 * Si el usuario todavía no completó su perfil, Django responde 404.
 */
public class MiPlanAlimenticioResponse {

    @SerializedName("requiere_revision")
    private boolean requiereRevision;

    private String mensaje;

    private AsignacionPlan asignacion;

    public boolean isRequiereRevision() {
        return requiereRevision;
    }

    public String getMensaje() {
        return mensaje;
    }

    public AsignacionPlan getAsignacion() {
        return asignacion;
    }
}
