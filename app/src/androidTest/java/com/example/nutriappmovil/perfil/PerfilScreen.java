package com.example.nutriappmovil.perfil;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.nutriappmovil.R;
import org.hamcrest.Matcher;
import java.io.File;
import java.io.FileOutputStream;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;

/** Screen Object: los IDs y los detalles de Espresso no se filtran al caso. */
public final class PerfilScreen {
    private static final int ESTADO = R.id.tvEstadoPerfil;
    private static final int GUARDAR = R.id.btnGuardarPerfil;
    private static final int EDAD = R.id.etEdad;
    private static final int PESO = R.id.etPesoActual;
    private static final int ALTURA = R.id.etAltura;
    private static final int OBJETIVO_PESO = R.id.etPesoObjetivo;
    private static final int DIAS = R.id.etDiasEntrenamiento;
    private static final int SEXO = R.id.spSexo;
    private static final int OBJETIVO = R.id.spObjetivo;
    private static final int ACTIVIDAD = R.id.spActividad;
    private static final int PREFERENCIA = R.id.spPreferencia;

    public PerfilScreen esperarPerfilNuevo() {
        esperarEstado(R.string.perfil_nuevo);
        return this;
    }

    public PerfilScreen completarDatosValidos() {
        escribir(EDAD, "30");
        escribir(PESO, "78");
        escribir(ALTURA, "175");
        escribir(OBJETIVO_PESO, "82");
        escribir(DIAS, "2");
        elegirPrimeraOpcion(SEXO);
        elegirPrimeraOpcion(OBJETIVO);
        elegirPrimeraOpcion(ACTIVIDAD);
        elegirPrimeraOpcion(PREFERENCIA);
        return this;
    }

    public PerfilScreen guardar() {
        onView(withId(GUARDAR)).perform(scrollTo(), click());
        return this;
    }

    public PerfilScreen comprobarGuardado() {
        esperarEstado(R.string.perfil_guardado);
        onView(withId(GUARDAR)).check(matches(isEnabled()));
        return this;
    }

    public PerfilScreen capturar(String nombre) throws Exception {
        Bitmap screenshot = InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        assertNotNull("Android no pudo capturar la pantalla", screenshot);
        File directory = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getExternalFilesDir("evidencias");
        assertNotNull(directory);
        File target = new File(directory, nombre + ".png");
        try (FileOutputStream output = new FileOutputStream(target)) {
            assertTrue(screenshot.compress(Bitmap.CompressFormat.PNG, 100, output));
        } finally { screenshot.recycle(); }
        return this;
    }

    private void escribir(int id, String value) {
        onView(withId(id)).perform(scrollTo(), replaceText(value), closeSoftKeyboard());
    }

    private void elegirPrimeraOpcion(int id) {
        onView(withId(id)).perform(scrollTo(), click());
        onData(anything()).atPosition(1).perform(click());
    }

    private void esperarEstado(int message) {
        onView(isRoot()).perform(new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isRoot(); }
            @Override public String getDescription() { return "Esperar resultado de Perfil"; }
            @Override public void perform(UiController controller, View root) {
                long deadline = SystemClock.uptimeMillis() + 10000;
                String expected = root.getContext().getString(message);
                do {
                    TextView state = root.findViewById(ESTADO);
                    if (state != null && expected.contentEquals(state.getText())) return;
                    controller.loopMainThreadForAtLeast(20);
                } while (SystemClock.uptimeMillis() < deadline);
                throw new AssertionError("No se alcanzo el resultado esperado: " + expected);
            }
        });
    }
}
