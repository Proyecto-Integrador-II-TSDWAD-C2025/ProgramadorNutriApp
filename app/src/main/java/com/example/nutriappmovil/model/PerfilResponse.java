package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

public class PerfilResponse {

    private Perfil perfil;

    @SerializedName("requiere_revision")
    private boolean requiereRevision;

    private String mensaje;

    public Perfil getPerfil() {
        return perfil;
    }

    public boolean isRequiereRevision() {
        return requiereRevision;
    }

    public String getMensaje() {
        return mensaje;
    }
}