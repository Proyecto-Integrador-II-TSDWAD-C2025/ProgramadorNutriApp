package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

public class PerfilRequest {

    private final String sexo;
    private final int edad;

    @SerializedName("peso_actual")
    private final double pesoActual;

    @SerializedName("altura_cm")
    private final int alturaCm;

    @SerializedName("peso_objetivo")
    private final double pesoObjetivo;

    private final String objetivo;
    private final String actividad;
    private final String preferencia;

    @SerializedName("dias_entrenamiento")
    private final int diasEntrenamiento;

    private final String limitaciones;

    @SerializedName("consideraciones_alimentarias")
    private final String consideracionesAlimentarias;

    public PerfilRequest(
            String sexo,
            int edad,
            double pesoActual,
            int alturaCm,
            double pesoObjetivo,
            String objetivo,
            String actividad,
            String preferencia,
            int diasEntrenamiento,
            String limitaciones,
            String consideracionesAlimentarias
    ) {
        this.sexo = sexo;
        this.edad = edad;
        this.pesoActual = pesoActual;
        this.alturaCm = alturaCm;
        this.pesoObjetivo = pesoObjetivo;
        this.objetivo = objetivo;
        this.actividad = actividad;
        this.preferencia = preferencia;
        this.diasEntrenamiento = diasEntrenamiento;
        this.limitaciones = limitaciones;
        this.consideracionesAlimentarias = consideracionesAlimentarias;
    }
}