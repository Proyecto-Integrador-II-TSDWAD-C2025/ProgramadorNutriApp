package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

public class Ejercicio {

    @SerializedName("id_ejercicio")
    private int idEjercicio;

    @SerializedName("dia")
    private int dia;

    @SerializedName("orden")
    private int orden;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("series")
    private Integer series;

    @SerializedName("repeticiones")
    private String repeticiones;

    @SerializedName("duracion_minutos")
    private Integer duracionMinutos;

    @SerializedName("completado_hoy")
    private boolean completadoHoy;

    public int getIdEjercicio() {
        return idEjercicio;
    }

    public void setIdEjercicio(int idEjercicio) {
        this.idEjercicio = idEjercicio;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getSeries() {
        return series;
    }

    public void setSeries(Integer series) {
        this.series = series;
    }

    public String getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(String repeticiones) {
        this.repeticiones = repeticiones;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public boolean isCompletadoHoy() {
        return completadoHoy;
    }

    public void setCompletadoHoy(boolean completadoHoy) {
        this.completadoHoy = completadoHoy;
    }
}
