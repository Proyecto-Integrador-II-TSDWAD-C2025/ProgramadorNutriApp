package com.example.nutriappmovil.perfil;

import com.example.nutriappmovil.model.Perfil;

/** Estado inmutable de carga; no conserva vistas, Context ni cuerpos HTTP. */
public final class EstadoCargaPerfil {
    public enum Tipo { CARGANDO, LISTO, NUEVO, SESION_INVALIDA, ERROR_HTTP, ERROR_CONEXION }

    private final Tipo tipo;
    private final Perfil perfil;

    public EstadoCargaPerfil(Tipo tipo, Perfil perfil) {
        this.tipo = tipo;
        this.perfil = perfil;
    }

    public Tipo getTipo() { return tipo; }
    public Perfil getPerfil() { return perfil; }
}
