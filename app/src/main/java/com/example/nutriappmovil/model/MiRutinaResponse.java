package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MiRutinaResponse {

    @SerializedName("requiere_revision")
    private boolean requiereRevision;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("asignacion")
    private Asignacion asignacion;

    public boolean isRequiereRevision() {
        return requiereRevision;
    }

    public void setRequiereRevision(boolean requiereRevision) {
        this.requiereRevision = requiereRevision;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Asignacion getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Asignacion asignacion) {
        this.asignacion = asignacion;
    }
}
