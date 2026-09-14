package com.example.nutriappmovil.data;

import com.example.nutriappmovil.model.Asignacion;
import com.example.nutriappmovil.model.Ejercicio;
import com.example.nutriappmovil.model.MiRutinaResponse;
import com.example.nutriappmovil.model.Rutina;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Datos mock locales que simulan la respuesta real del backend.
 * En un sprint futuro se reemplaza la llamada a estos metodos por la llamada
 * al endpoint GET /api/mi-rutina/ sin modificar los modelos ni la UI.
 */
public class MockMiRutinaData {

    /**
     * Caso feliz: rutina asignada con 3 dias de entrenamiento.
     * Incluye ejercicio por duracion (plancha) y uno marcado como completado hoy.
     */
    public static MiRutinaResponse getRutinaCompleta() {
        MiRutinaResponse response = new MiRutinaResponse();
        response.setRequiereRevision(false);
        response.setMensaje("Rutina recomendada correctamente.");
        response.setAsignacion(crearAsignacionCompleta());
        return response;
    }

    /**
     * Caso sin asignacion: el usuario todavia no tiene rutina asignada.
     */
    public static MiRutinaResponse getSinAsignacion() {
        MiRutinaResponse response = new MiRutinaResponse();
        response.setRequiereRevision(false);
        response.setMensaje("Todavia no tenes una rutina asignada.");
        response.setAsignacion(null);
        return response;
    }

    /**
     * Caso en revision: la rutina esta siendo revisada por un profesional.
     */
    public static MiRutinaResponse getEnRevision() {
        MiRutinaResponse response = new MiRutinaResponse();
        response.setRequiereRevision(true);
        response.setMensaje("Tu rutina esta siendo revisada por el equipo nutricional.");
        response.setAsignacion(crearAsignacionCompleta());
        return response;
    }

    private static Asignacion crearAsignacionCompleta() {
        Asignacion asignacion = new Asignacion();
        asignacion.setIdUsuarioRutina(42);
        asignacion.setMotivo("Perfil compatible: objetivo 'aumentar_masa', nivel 'inicial', 3 dias disponibles por semana.");
        asignacion.setFechaAsignacion("2025-09-13T14:30:00Z");
        asignacion.setRutina(crearRutina());
        return asignacion;
    }

    private static Rutina crearRutina() {
        Rutina rutina = new Rutina();
        rutina.setIdRutina(5);
        rutina.setNombre("Fuerza inicial");
        rutina.setDescripcion("Rutina orientativa de fuerza para principiantes con 3 dias de entrenamiento por semana.");
        rutina.setObjetivo("aumentar_masa");
        rutina.setNivel("inicial");
        rutina.setDiasPorSemana(3);
        rutina.setDuracionSemanas(4);
        rutina.setEjercicios(crearEjercicios());
        return rutina;
    }

    private static List<Ejercicio> crearEjercicios() {
        List<Ejercicio> ejercicios = new ArrayList<>();

        // Dia 1
        ejercicios.add(crearEjercicio(101, 1, 1, "Sentadillas con peso corporal",
                "Mantener la espalda recta y bajar hasta que los muslos esten paralelos al suelo.",
                3, "12-15", null, false));

        ejercicios.add(crearEjercicio(102, 1, 2, "Flexiones de pecho",
                "Mantener el cuerpo recto, bajar controladamente hasta tocar el pecho al suelo.",
                3, "10-12", null, true)); // completado_hoy = true

        ejercicios.add(crearEjercicio(103, 1, 3, "Plancha frontal",
                "Mantener el cuerpo alineado desde hombros hasta talones, contrayendo el core.",
                null, "", 5, false)); // duracion_minutos, series null, repeticiones vacio

        // Dia 2
        ejercicios.add(crearEjercicio(104, 2, 1, "Puentes de gluteos",
                "Acostado boca arriba, elevar la cadera contrayendo gluteos.",
                3, "15", null, false));

        ejercicios.add(crearEjercicio(105, 2, 2, "Sentadilla bulgara",
                "Un pie adelante sobre una superficie elevada, bajar en sentadilla controlada.",
                3, "10", null, false));

        // Dia 3
        ejercicios.add(crearEjercicio(106, 3, 1, "Fondos en silla",
                "Manos apoyadas en el borde de una silla, bajar flexionando los codos.",
                3, "12", null, false));

        ejercicios.add(crearEjercicio(107, 3, 2, "Plancha lateral",
                "Apoyar el antebrazo y mantener el cuerpo recto durante el tiempo indicado.",
                null, "", 3, false)); // duracion_minutos

        return ejercicios;
    }

    private static Ejercicio crearEjercicio(int id, int dia, int orden, String nombre,
                                            String descripcion, Integer series,
                                            String repeticiones, Integer duracionMinutos,
                                            boolean completadoHoy) {
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setIdEjercicio(id);
        ejercicio.setDia(dia);
        ejercicio.setOrden(orden);
        ejercicio.setNombre(nombre);
        ejercicio.setDescripcion(descripcion);
        ejercicio.setSeries(series);
        ejercicio.setRepeticiones(repeticiones);
        ejercicio.setDuracionMinutos(duracionMinutos);
        ejercicio.setCompletadoHoy(completadoHoy);
        return ejercicio;
    }
}
