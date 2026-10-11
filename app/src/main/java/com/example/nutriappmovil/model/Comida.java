package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Comida del catálogo (ComidaSerializer, fields = '__all__'). Llega anidada
 * dentro de cada PlanComida, en el campo "id_comida".
 *
 * OJO: calorias, proteinas, carbohidratos y grasas son DecimalField en Django
 * y DRF los manda como texto ("340.00"). Gson los convierte a double.
 */
public class Comida {

    @SerializedName("id_comida")
    private int idComida;

    private String nombre;

    // frutas_verduras | cereales_legumbres | proteinas | lacteos | grasas_saludables | preparacion
    private String categoria;

    @SerializedName("porcion_referencia")
    private String porcionReferencia;

    private double calorias;

    private double proteinas;

    private double carbohidratos;

    private double grasas;

    public int getIdComida() {
        return idComida;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getPorcionReferencia() {
        return porcionReferencia;
    }

    public double getCalorias() {
        return calorias;
    }

    public double getProteinas() {
        return proteinas;
    }

    public double getCarbohidratos() {
        return carbohidratos;
    }

    public double getGrasas() {
        return grasas;
    }
}
