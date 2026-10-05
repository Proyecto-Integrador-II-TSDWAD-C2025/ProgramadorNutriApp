package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

public class CompletarEjercicioResponse {

    @SerializedName("ejercicio_id")
    private int ejercicioId;

    @SerializedName("completado_hoy")
    private boolean completadoHoy;

    public int getEjercicioId() {
        return ejercicioId;
    }

    public void setEjercicioId(int ejercicioId) {
        this.ejercicioId = ejercicioId;
    }

    public boolean isCompletadoHoy() {
        return completadoHoy;
    }

    public void setCompletadoHoy(boolean completadoHoy) {
        this.completadoHoy = completadoHoy;
    }
}
