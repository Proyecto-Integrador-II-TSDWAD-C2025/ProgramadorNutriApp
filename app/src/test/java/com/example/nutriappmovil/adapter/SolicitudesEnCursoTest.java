package com.example.nutriappmovil.adapter;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SolicitudesEnCursoTest {

    @Test
    public void iniciar_rechazaUnSegundoToqueSobreLaMismaComida() {
        SolicitudesEnCurso enCurso = new SolicitudesEnCurso();

        assertTrue(enCurso.iniciar(25));
        assertFalse(enCurso.iniciar(25));
        assertTrue(enCurso.contiene(25));
    }

    @Test
    public void iniciar_permiteOtraComidaAlMismoTiempo() {
        SolicitudesEnCurso enCurso = new SolicitudesEnCurso();

        assertTrue(enCurso.iniciar(25));
        assertTrue(enCurso.iniciar(26));
    }

    @Test
    public void finalizar_habilitaUnNuevoCambioSobreLaMismaComida() {
        SolicitudesEnCurso enCurso = new SolicitudesEnCurso();
        enCurso.iniciar(25);

        enCurso.finalizar(25);

        assertFalse(enCurso.contiene(25));
        assertTrue(enCurso.iniciar(25));
    }

    @Test
    public void limpiar_descartaTodasLasSolicitudes() {
        SolicitudesEnCurso enCurso = new SolicitudesEnCurso();
        enCurso.iniciar(25);
        enCurso.iniciar(26);

        enCurso.limpiar();

        assertFalse(enCurso.contiene(25));
        assertFalse(enCurso.contiene(26));
    }
}
