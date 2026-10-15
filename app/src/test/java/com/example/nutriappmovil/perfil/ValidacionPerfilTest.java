package com.example.nutriappmovil.perfil;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidacionPerfilTest {
    @Test public void edadValida_aceptaValorInterior() {
        // Arrange
        String edad = "30";
        // Act
        boolean valida = ValidacionPerfil.enteroEnRango(edad, 13, 100);
        // Assert
        assertTrue(valida);
    }

    @Test public void edadVacia_rechazaCampoObligatorio() {
        // Arrange
        String edad = "";
        // Act
        boolean valida = ValidacionPerfil.enteroEnRango(edad, 13, 100);
        // Assert
        assertFalse(valida);
    }

    @Test public void edadEnLimiteInferior_aceptaTrece() {
        // Arrange
        String edad = "13";
        // Act
        boolean valida = ValidacionPerfil.enteroEnRango(edad, 13, 100);
        // Assert
        assertTrue(valida);
    }

    @Test public void edadFueraDeFormato_rechazaTexto() {
        // Arrange
        String edad = "treinta";
        // Act
        boolean valida = ValidacionPerfil.enteroEnRango(edad, 13, 100);
        // Assert
        assertFalse(valida);
    }

    @Test public void aceptaLimitesDeEdadYDias() {
        assertTrue(ValidacionPerfil.enteroEnRango("13", 13, 100));
        assertTrue(ValidacionPerfil.enteroEnRango("100", 13, 100));
        assertTrue(ValidacionPerfil.enteroEnRango("1", 1, 6));
        assertTrue(ValidacionPerfil.enteroEnRango("6", 1, 6));
        assertFalse(ValidacionPerfil.enteroEnRango("12", 13, 100));
        assertFalse(ValidacionPerfil.enteroEnRango("101", 13, 100));
        assertFalse(ValidacionPerfil.enteroEnRango("7", 1, 6));
    }

    @Test public void rechazaEnterosVaciosFraccionariosODesbordados() {
        for (String texto : new String[] {null, "", " ", "30.5", "NaN", "9999999999999999999"}) {
            assertFalse(ValidacionPerfil.enteroEnRango(texto, 13, 100));
        }
    }

    @Test public void aceptaComaPuntoYLimitesDePeso() {
        for (String texto : new String[] {"30", "300", "70,50", "70.50", " 70,5 ", "70."}) {
            assertTrue(texto, ValidacionPerfil.decimalEnRango(texto, 30, 300));
        }
        assertFalse(ValidacionPerfil.decimalEnRango("29.99", 30, 300));
        assertFalse(ValidacionPerfil.decimalEnRango("300.01", 30, 300));
    }

    @Test public void rechazaNoFinitosExponentesYSufijosJavaAntesDeSerializar() {
        for (String texto : new String[] {null, "", "NaN", "Infinity", "1e2", "70d", "70.123", "70,2,3", "abc"}) {
            assertFalse(String.valueOf(texto), ValidacionPerfil.decimalEnRango(texto, 30, 300));
        }
    }
}
