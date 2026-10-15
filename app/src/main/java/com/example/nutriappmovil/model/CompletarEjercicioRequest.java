package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

public class CompletarEjercicioRequest {

    @SerializedName("ejercicio_id")
    private int ejercicioId;

    public CompletarEjercicioRequest(int ejercicioId) {
        this.ejercicioId = ejercicioId;
    }

    public int getEjercicioId() {
        return ejercicioId;
    }

    public void setEjercicioId(int ejercicioId) {
        this.ejercicioId = ejercicioId;
    }
}
