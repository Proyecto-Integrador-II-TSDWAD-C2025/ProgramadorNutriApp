package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Plan alimenticio devuelto por /api/planes/ (PlanSerializer, fields = '__all__').
 * Campos confirmados contra models.py (clase Plan).
 *
 * OJO: calorias_objetivo es DecimalField en Django y DRF lo manda como texto
 * ("2000.00"). Gson lo convierte a double sin problema; con int fallaría si
 * llega un decimal.
 */
public class Plan {

    @SerializedName("id_plan")
    private int idPlan;

    // Único y puede ser null
    private String codigo;

    @SerializedName("nombre_plan")
    private String nombrePlan;

    private String descripcion;

    @SerializedName("duracion_dias")
    private int duracionDias;

    @SerializedName("calorias_objetivo")
    private double caloriasObjetivo;

    // bajar_grasa | mantener_peso | aumentar_masa | mejorar_habitos
    private String objetivo;

    // bajo | moderado | alto
    @SerializedName("nivel_actividad")
    private String nivelActividad;

    // todas | vegetariana | alta_proteina | baja_calorias
    @SerializedName("preferencia_compatible")
    private String preferenciaCompatible;

    private String observaciones;

    private boolean activo;

    public int getIdPlan() {
        return idPlan;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getDuracionDias() {
        return duracionDias;
    }

    public double getCaloriasObjetivo() {
        return caloriasObjetivo;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public String getNivelActividad() {
        return nivelActividad;
    }

    public String getPreferenciaCompatible() {
        return preferenciaCompatible;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public boolean isActivo() {
        return activo;
    }
}
