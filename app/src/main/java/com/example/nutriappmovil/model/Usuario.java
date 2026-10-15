package com.example.nutriappmovil.model;

public class Usuario {

    private int id_usuario;
    private String nombre;
    private String apellido;
    private String email;
    private String fecha_registro;
    private Rol id_rol;
    private boolean is_active;

    public int getIdUsuario() {
        return id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getFechaRegistro() {
        return fecha_registro;
    }

    public Rol getRol() {
        return id_rol;
    }

    public boolean isActive() {
        return is_active;
    }
}