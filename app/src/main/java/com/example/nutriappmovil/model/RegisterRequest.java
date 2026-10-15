package com.example.nutriappmovil.model;

public class RegisterRequest {
    private String email;
    private String contrasena;
    private String nombre;
    private String apellido;

    public RegisterRequest(String email, String contrasena, String nombre, String apellido) {
        this.email = email;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.apellido = apellido;
    }
}