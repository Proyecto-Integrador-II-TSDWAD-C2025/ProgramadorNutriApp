package com.example.nutriappmovil.model;

import com.google.gson.Gson;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Los JSON de src/test/resources/plan_alimenticio son respuestas reales del
 * backend (capturadas el 10/10/2026 con una cuenta de prueba).
 */
public class PlanAlimenticioModelTest {

    private static final double DELTA = 0.001;

    private final Gson gson = new Gson();

    @Test
    public void miPlan_conAsignacion_leeAsignacionYPlan() throws IOException {
        MiPlanAlimenticioResponse response = leer(
                "mi_plan_con_asignacion.json",
                MiPlanAlimenticioResponse.class
        );

        assertFalse(response.isRequiereRevision());
        assertEquals("", response.getMensaje());

        AsignacionPlan asignacion = response.getAsignacion();
        assertNotNull(asignacion);
        assertEquals(2, asignacion.getIdUsuarioPlan());
        assertEquals("2026-10-10", asignacion.getFechaInicio());
        assertEquals("2026-10-23", asignacion.getFechaFin());
        assertEquals("activo", asignacion.getEstado());
        assertEquals("orientativo", asignacion.getOrigen());
        assertTrue(asignacion.getMotivo().startsWith("Sugerencia orientativa"));

        PlanDetalle plan = asignacion.getPlan();
        assertNotNull(plan);
        assertEquals(3, plan.getIdPlan());
        assertEquals("descenso_equilibrado", plan.getCodigo());
        assertEquals("Descenso equilibrado", plan.getNombrePlan());
        assertEquals(14, plan.getDuracionDias());
        // Django lo manda como texto: "1800.00"
        assertEquals(1800.0, plan.getCaloriasObjetivo(), DELTA);
        assertEquals("bajar_grasa", plan.getObjetivo());
        assertEquals("moderado", plan.getNivelActividad());
        assertEquals("todas", plan.getPreferenciaCompatible());
        assertTrue(plan.isActivo());
    }

    @Test
    public void miPlan_conAsignacion_leeComidasConSuDetalle() throws IOException {
        MiPlanAlimenticioResponse response = leer(
                "mi_plan_con_asignacion.json",
                MiPlanAlimenticioResponse.class
        );

        List<PlanComida> comidas = response.getAsignacion().getPlan().getComidasPlan();
        assertEquals(12, comidas.size());

        PlanComida primera = comidas.get(0);
        assertEquals(25, primera.getIdPlanComida());
        assertEquals(3, primera.getIdPlan());
        assertEquals(1, primera.getDia());
        assertEquals(1, primera.getOrden());
        assertEquals("desayuno", primera.getTipoComida());
        assertEquals("1 bol", primera.getPorcion());
        assertEquals("Tostadas integrales con queso y fruta", primera.getAlternativa());
        assertFalse(primera.isCompletadaHoy());

        // "id_comida" trae el objeto completo, con decimales como texto
        Comida comida = primera.getComida();
        assertNotNull(comida);
        assertEquals(1, comida.getIdComida());
        assertEquals("Yogur natural con avena y fruta", comida.getNombre());
        assertEquals("preparacion", comida.getCategoria());
        assertEquals("1 bol", comida.getPorcionReferencia());
        assertEquals(340.0, comida.getCalorias(), DELTA);
        assertEquals(15.0, comida.getProteinas(), DELTA);
        assertEquals(52.0, comida.getCarbohidratos(), DELTA);
        assertEquals(8.0, comida.getGrasas(), DELTA);

        PlanComida ultima = comidas.get(comidas.size() - 1);
        assertEquals(3, ultima.getDia());
        assertEquals(4, ultima.getOrden());
        assertEquals("cena", ultima.getTipoComida());
    }

    @Test
    public void miPlan_enRevision_vieneSinAsignacionYConMensaje() throws IOException {
        MiPlanAlimenticioResponse response = leer(
                "mi_plan_en_revision.json",
                MiPlanAlimenticioResponse.class
        );

        assertTrue(response.isRequiereRevision());
        assertNull(response.getAsignacion());
        assertTrue(response.getMensaje().contains("revisado por un profesional"));
    }

    @Test
    public void completarComida_respuesta_leeIdYEstado() throws IOException {
        CompletarComidaResponse response = leer(
                "completar_comida_marcada.json",
                CompletarComidaResponse.class
        );

        assertEquals(25, response.getPlanComidaId());
        assertTrue(response.isCompletadaHoy());
    }

    @Test
    public void completarComida_request_usaElNombreDeCampoDeDjango() {
        assertEquals(
                "{\"plan_comida_id\":25}",
                gson.toJson(new CompletarComidaRequest(25))
        );
    }

    private <T> T leer(String archivo, Class<T> tipo) throws IOException {
        InputStream stream = getClass()
                .getClassLoader()
                .getResourceAsStream("plan_alimenticio/" + archivo);
        assertNotNull("No se encontró el recurso " + archivo, stream);

        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, tipo);
        }
    }
}
