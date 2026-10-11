package com.example.nutriappmovil.adapter;

import com.example.nutriappmovil.model.MiPlanAlimenticioResponse;
import com.example.nutriappmovil.model.PlanComida;
import com.google.gson.Gson;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MenuPlanAlimenticioTest {

    @Test
    public void agruparPorDia_intercalaUnEncabezadoPorDia() throws IOException {
        List<Object> items = MenuPlanAlimenticio.agruparPorDia(comidasReales());

        // 12 comidas en 3 días: 3 encabezados + 12 comidas
        assertEquals(15, items.size());
        assertEquals(1, items.get(0));
        assertEquals(2, items.get(5));
        assertEquals(3, items.get(10));
        assertTrue(items.get(1) instanceof PlanComida);
    }

    @Test
    public void agruparPorDia_ordenaPorDiaYOrdenAunqueLleguenDesordenadas() throws IOException {
        List<PlanComida> desordenadas = new ArrayList<>(comidasReales());
        Collections.reverse(desordenadas);
        // Un elemento null no debe romper el armado
        desordenadas.add(null);

        List<Object> items = MenuPlanAlimenticio.agruparPorDia(desordenadas);

        assertEquals(15, items.size());

        PlanComida primera = (PlanComida) items.get(1);
        assertEquals(1, primera.getDia());
        assertEquals(1, primera.getOrden());
        assertEquals("desayuno", primera.getTipoComida());

        PlanComida ultima = (PlanComida) items.get(14);
        assertEquals(3, ultima.getDia());
        assertEquals(4, ultima.getOrden());
        assertEquals("cena", ultima.getTipoComida());
    }

    @Test
    public void agruparPorDia_sinComidasDevuelveListaVacia() {
        assertTrue(MenuPlanAlimenticio.agruparPorDia(null).isEmpty());
        assertTrue(MenuPlanAlimenticio.agruparPorDia(new ArrayList<PlanComida>()).isEmpty());
    }

    @Test
    public void formatearCantidad_quitaDecimalesInnecesarios() {
        assertEquals("340", MenuPlanAlimenticio.formatearCantidad(340.0));
        assertEquals("1800", MenuPlanAlimenticio.formatearCantidad(1800.00));
        assertEquals("12,5", MenuPlanAlimenticio.formatearCantidad(12.5));
        assertEquals("0", MenuPlanAlimenticio.formatearCantidad(0));
    }

    @Test
    public void formatearFecha_pasaDeFormatoIsoADiaMesAnio() {
        assertEquals("10/10/2026", MenuPlanAlimenticio.formatearFecha("2026-10-10"));
        assertEquals("23/10/2026", MenuPlanAlimenticio.formatearFecha(" 2026-10-23 "));
    }

    @Test
    public void formatearFecha_toleraValoresInesperados() {
        assertEquals("", MenuPlanAlimenticio.formatearFecha(null));
        assertEquals("sin fecha", MenuPlanAlimenticio.formatearFecha("sin fecha"));
    }

    private List<PlanComida> comidasReales() throws IOException {
        InputStream stream = getClass()
                .getClassLoader()
                .getResourceAsStream("plan_alimenticio/mi_plan_con_asignacion.json");
        assertNotNull(stream);

        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            MiPlanAlimenticioResponse response = new Gson()
                    .fromJson(reader, MiPlanAlimenticioResponse.class);
            return response.getAsignacion().getPlan().getComidasPlan();
        }
    }
}
