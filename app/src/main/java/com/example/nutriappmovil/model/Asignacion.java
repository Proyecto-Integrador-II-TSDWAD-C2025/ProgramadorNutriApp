package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

public class Asignacion {

    @SerializedName("id_usuario_rutina")
    private int idUsuarioRutina;

    @SerializedName("motivo")
    private String motivo;

    @SerializedName("fecha_asignacion")
    private String fechaAsignacion;

    @SerializedName("rutina")
    private Rutina rutina;

    public int getIdUsuarioRutina() {
        return idUsuarioRutina;
    }

    public void setIdUsuarioRutina(int idUsuarioRutina) {
        this.idUsuarioRutina = idUsuarioRutina;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(String fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public Rutina getRutina() {
        return rutina;
    }

    public void setRutina(Rutina rutina) {
        this.rutina = rutina;
    }
}
