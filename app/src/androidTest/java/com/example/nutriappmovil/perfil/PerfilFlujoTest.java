package com.example.nutriappmovil.perfil;

import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.nutriappmovil.PerfilActivity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

/** AUT-UI-01/03 y ACC-01. Pendiente ejecutar en Android; compilar no equivale a PASS. */
@RunWith(AndroidJUnit4.class)
public class PerfilFlujoTest {
    private final PerfilRedSimuladaRule red = new PerfilRedSimuladaRule();
    @Rule public RuleChain rules = RuleChain.outerRule(red)
            .around(new ActivityScenarioRule<>(PerfilActivity.class));

    @BeforeClass public static void activarAccesibilidad() {
        AccessibilityChecks.enable().setRunChecksFromRootView(true);
    }

    @AfterClass public static void restaurarChequeos() { AccessibilityChecks.disable(); }

    @Test public void datosValidos_guardaPerfilYCapturaResultado() throws Exception {
        // Arrange
        PerfilScreen pantalla = new PerfilScreen().esperarPerfilNuevo();
        // Act
        pantalla.completarDatosValidos().guardar();
        // Assert
        pantalla.comprobarGuardado().capturar("TC-PER-UI-01-guardado");
        assertEquals(1, red.getGuardados());
    }
}
