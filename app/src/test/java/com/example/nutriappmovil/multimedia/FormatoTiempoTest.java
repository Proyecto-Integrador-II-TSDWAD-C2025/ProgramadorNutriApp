package com.example.nutriappmovil.multimedia;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FormatoTiempoTest {

    @Test
    public void minutosYSegundos_completaLosSegundosConCero() {
        assertEquals("0:00", FormatoTiempo.minutosYSegundos(0));
        assertEquals("0:05", FormatoTiempo.minutosYSegundos(5000));
        assertEquals("0:24", FormatoTiempo.minutosYSegundos(24800));
        assertEquals("1:05", FormatoTiempo.minutosYSegundos(65000));
    }

    @Test
    public void minutosYSegundos_tomaUnValorNegativoComoCero() {
        // VideoView devuelve -1 cuando todavía no conoce la duración
        assertEquals("0:00", FormatoTiempo.minutosYSegundos(-1));
    }

    @Test
    public void porcentaje_calculaElAvance() {
        assertEquals(0, FormatoTiempo.porcentaje(0, 24800));
        assertEquals(50, FormatoTiempo.porcentaje(12400, 24800));
        assertEquals(100, FormatoTiempo.porcentaje(24800, 24800));
    }

    @Test
    public void porcentaje_noSeSaleDelRango() {
        assertEquals(100, FormatoTiempo.porcentaje(30000, 24800));
        assertEquals(0, FormatoTiempo.porcentaje(-1, 24800));
        assertEquals(0, FormatoTiempo.porcentaje(5000, 0));
        assertEquals(0, FormatoTiempo.porcentaje(5000, -1));
    }
}
