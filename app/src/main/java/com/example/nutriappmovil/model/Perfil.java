package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

public class Perfil {

    @SerializedName("id_perfil")
    private int idPerfil;

    private String sexo;

    private int edad;

    @SerializedName("peso_actual")
    private String pesoActual;

    @SerializedName("altura_cm")
    private int alturaCm;

    @SerializedName("peso_objetivo")
    private String pesoObjetivo;

    private String objetivo;

    private String actividad;

    private String preferencia;

    @SerializedName("dias_entrenamiento")
    private int diasEntrenamiento;

    private String limitaciones;

    @SerializedName("consideraciones_alimentarias")
    private String consideracionesAlimentarias;

    @SerializedName("fecha_actualizacion")
    private String fechaActualizacion;

    public int getIdPerfil() {
        return idPerfil;
    }

    public String getSexo() {
        return sexo;
    }

    public int getEdad() {
        return edad;
    }

    public String getPesoActual() {
        return pesoActual;
    }

    public int getAlturaCm() {
        return alturaCm;
    }

    public String getPesoObjetivo() {
        return pesoObjetivo;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public String getActividad() {
        return actividad;
    }

    public String getPreferencia() {
        return preferencia;
    }

    public int getDiasEntrenamiento() {
        return diasEntrenamiento;
    }

    public String getLimitaciones() {
        return limitaciones;
    }

    public String getConsideracionesAlimentarias() {
        return consideracionesAlimentarias;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }
}