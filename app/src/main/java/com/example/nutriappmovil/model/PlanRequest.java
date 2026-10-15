package com.example.nutriappmovil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Cuerpo para crear (POST) o editar (PUT) un plan en /api/planes/.
 * Mismo estilo que PerfilRequest: campos private final + constructor.
 *
 * Obligatorios en Django: nombre_plan, descripcion, duracion_dias y
 * calorias_objetivo. El resto tiene valor por defecto.
 * Rangos que valida el serializer: duracion_dias 1-365, calorias_objetivo 500-6000.
 * "codigo" no se manda: es opcional y único.
 */
public class PlanRequest {

    @SerializedName("nombre_plan")
    private final String nombrePlan;

    private final String descripcion;

    @SerializedName("duracion_dias")
    private final int duracionDias;

    @SerializedName("calorias_objetivo")
    private final double caloriasObjetivo;

    // bajar_grasa | mantener_peso | aumentar_masa | mejorar_habitos
    private final String objetivo;

    // bajo | moderado | alto
    @SerializedName("nivel_actividad")
    private final String nivelActividad;

    // todas | vegetariana | alta_proteina | baja_calorias
    @SerializedName("preferencia_compatible")
    private final String preferenciaCompatible;

    private final String observaciones;

    private final boolean activo;

    public PlanRequest(
            String nombrePlan,
            String descripcion,
            int duracionDias,
            double caloriasObjetivo,
            String objetivo,
            String nivelActividad,
            String preferenciaCompatible,
            String observaciones,
            boolean activo
    ) {
        this.nombrePlan = nombrePlan;
        this.descripcion = descripcion;
        this.duracionDias = duracionDias;
        this.caloriasObjetivo = caloriasObjetivo;
        this.objetivo = objetivo;
        this.nivelActividad = nivelActividad;
        this.preferenciaCompatible = preferenciaCompatible;
        this.observaciones = observaciones;
        this.activo = activo;
    }
}
