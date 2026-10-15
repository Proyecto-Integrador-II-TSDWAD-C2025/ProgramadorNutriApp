
package com.example.nutriappmovil.model;

public class EditarUsuarioRequest {

    private String nombre;
    private String apellido;
    private String email;
    private int id_rol;

    public EditarUsuarioRequest(
            String nombre,
            String apellido,
            String email,
            int id_rol
    ) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.id_rol = id_rol;
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

    public int getId_rol() {
        return id_rol;
    }
}
