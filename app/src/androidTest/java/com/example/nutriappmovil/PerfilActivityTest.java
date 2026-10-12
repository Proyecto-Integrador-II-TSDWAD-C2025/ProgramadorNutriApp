package com.example.nutriappmovil;

import android.os.SystemClock;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;

import com.example.nutriappmovil.network.ApiClient;
import com.google.gson.Gson;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

/** Respuestas interceptadas en memoria: estos tests nunca llaman a Railway. */
@RunWith(AndroidJUnit4.class)
public class PerfilActivityTest {
    private static final String PERFIL = "{\"perfil\":{\"sexo\":\"m\",\"edad\":30,"
            + "\"peso_actual\":\"78.00\",\"altura_cm\":175,\"peso_objetivo\":\"82.00\","
            + "\"objetivo\":\"aumentar_masa\",\"actividad\":\"bajo\","
            + "\"preferencia\":\"sin_preferencia\",\"dias_entrenamiento\":2,"
            + "\"limitaciones\":\"\",\"consideraciones_alimentarias\":\"\"}}";
    private final AtomicInteger getCode = new AtomicInteger(404);
    private final AtomicInteger putCode = new AtomicInteger(200);
    private final AtomicInteger getRequests = new AtomicInteger();
    private final AtomicInteger putRequests = new AtomicInteger();
    private volatile CountDownLatch putGate;
    private Field retrofitField;
    private Object previousRetrofit;
    private ActivityScenario<PerfilActivity> scenario;

    @Before public void setUp() throws Exception {
        retrofitField = ApiClient.class.getDeclaredField("retrofit");
        retrofitField.setAccessible(true);
        previousRetrofit = retrofitField.get(null);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            boolean saving = chain.request().method().equals("PUT");
            int code;
            if (saving) {
                putRequests.incrementAndGet();
                CountDownLatch gate = putGate;
                if (gate != null) {
                    try {
                        if (!gate.await(5, TimeUnit.SECONDS)) throw new AssertionError("Guardado bloqueado");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new AssertionError(e);
                    }
                }
                code = putCode.get();
            } else {
                getRequests.incrementAndGet();
                code = getCode.get();
            }
            String body = code == 200 ? PERFIL
                    : code == 400 ? "{\"edad\":[\"Edad rechazada por la API\"]}"
                    : "{\"detail\":\"Respuesta de prueba\"}";
            return new okhttp3.Response.Builder().request(chain.request())
                    .protocol(Protocol.HTTP_1_1).code(code).message("Test")
                    .body(ResponseBody.create(MediaType.get("application/json"), body)).build();
        }).build();
        retrofitField.set(null, new Retrofit.Builder().baseUrl("https://example.invalid/api/")
                .client(client).addConverterFactory(GsonConverterFactory.create(new Gson())).build());
    }

    @After public void tearDown() throws Exception {
        if (putGate != null) putGate.countDown();
        if (scenario != null) scenario.close();
        retrofitField.set(null, previousRetrofit);
    }

    @Test public void cargaFallidaBloqueaGuardadoYPermiteReintentar() {
        getCode.set(500);
        scenario = ActivityScenario.launch(PerfilActivity.class);
        esperarEstado(R.string.perfil_error_carga);
        onView(withId(R.id.btnGuardarPerfil)).check(matches(not(isEnabled())));
        getCode.set(404);
        onView(withId(R.id.btnReintentarPerfil)).perform(scrollTo(), click());
        esperarEstado(R.string.perfil_nuevo);
        onView(withId(R.id.btnGuardarPerfil)).check(matches(isEnabled()));
        assertEquals(2, getRequests.get());
    }

    @Test public void sesionInvalidaBloqueaEdicionSinCambiarLogin() {
        getCode.set(401);
        scenario = ActivityScenario.launch(PerfilActivity.class);
        esperarEstado(R.string.perfil_sesion_invalida);
        onView(withId(R.id.etEdad)).check(matches(not(isEnabled())));
        onView(withId(R.id.btnGuardarPerfil)).check(matches(not(isEnabled())));
    }

    @Test public void muestraError400EnElCampoCorrespondiente() {
        abrirPerfilNuevo();
        completarFormulario();
        putCode.set(400);
        pulsarGuardar();
        esperarEstado(R.string.perfil_revisar_datos);
        onView(withId(R.id.etEdad)).check(matches(hasErrorText("Edad rechazada por la API")));
        onView(withId(R.id.btnGuardarPerfil)).check(matches(isEnabled()));
    }

    @Test public void falloDeGuardadoConservaDatosYPermiteReintentar() {
        abrirPerfilNuevo();
        completarFormulario();
        putCode.set(500);
        pulsarGuardar();
        esperarEstado(R.string.perfil_error_guardado);
        onView(withId(R.id.etPesoActual)).check(matches(withText("78,00")));
        onView(withId(R.id.btnGuardarPerfil)).check(matches(isEnabled()));
        putCode.set(200);
        pulsarGuardar();
        esperarEstado(R.string.perfil_guardado);
        onView(withId(R.id.etPesoActual)).check(matches(withText("78.00")));
    }

    @Test public void dobleClickNoDuplicaPutYLaRotacionConservaEdicion() {
        abrirPerfilNuevo();
        completarFormulario();
        scenario.recreate();
        esperarEstado(R.string.perfil_listo);
        onView(withId(R.id.etPesoActual)).check(matches(withText("78,00")));
        assertEquals(1, getRequests.get());
        putGate = new CountDownLatch(1);
        scenario.onActivity(activity -> {
            Button button = activity.findViewById(R.id.btnGuardarPerfil);
            button.performClick();
            assertFalse(button.isEnabled());
            button.performClick();
        });
        putGate.countDown();
        esperarEstado(R.string.perfil_guardado);
        assertEquals(1, putRequests.get());
    }

    @Test public void validacionLocalNoEnviaValoresFueraDeRango() {
        abrirPerfilNuevo();
        completarFormulario();
        scenario.onActivity(activity -> ((EditText) activity.findViewById(R.id.etEdad)).setText("12"));
        pulsarGuardar();
        esperarEstado(R.string.perfil_revisar_datos);
        String message = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getString(R.string.perfil_error_edad);
        onView(withId(R.id.etEdad)).check(matches(hasErrorText(message)));
        assertEquals(0, putRequests.get());
    }

    @Test public void etiquetasEncabezadoYAlturaAdaptableEstanConfigurados() {
        abrirPerfilNuevo();
        scenario.onActivity(activity -> {
            int[] fields = {R.id.spSexo, R.id.etEdad, R.id.etPesoActual, R.id.etAltura,
                    R.id.etPesoObjetivo, R.id.spObjetivo, R.id.spActividad,
                    R.id.etDiasEntrenamiento, R.id.spPreferencia, R.id.etLimitaciones,
                    R.id.etConsideracionesAlimentarias};
            View root = activity.findViewById(android.R.id.content);
            for (int id : fields) assertTrue("Falta etiqueta para " + id, tieneEtiqueta(root, id));
            assertTrue(ViewCompat.isAccessibilityHeading(activity.findViewById(R.id.tvTituloPerfil)));
            assertEquals(View.ACCESSIBILITY_LIVE_REGION_POLITE,
                    activity.findViewById(R.id.tvEstadoPerfil).getAccessibilityLiveRegion());
            View volver = activity.findViewById(R.id.btnVolverPerfil);
            assertEquals(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, volver.getLayoutParams().height);
            assertTrue(volver.getHeight() / activity.getResources().getDisplayMetrics().density >= 48f);
        });
    }

    @Test public void guardarMantieneContrasteNormalYPresionado() {
        abrirPerfilNuevo();
        scenario.onActivity(activity -> {
            Button guardar = activity.findViewById(R.id.btnGuardarPerfil);
            for (boolean pressed : new boolean[] {false, true}) {
                guardar.setPressed(pressed);
                GradientDrawable fondo = (GradientDrawable) guardar.getBackground().getCurrent();
                assertNotNull(fondo.getColor());
                double contraste = ColorUtils.calculateContrast(
                        guardar.getCurrentTextColor(), fondo.getColor().getDefaultColor());
                assertTrue("Contraste insuficiente: " + contraste, contraste >= 4.5);
            }
            guardar.setPressed(false);
            assertEquals(0, putRequests.get());
        });
    }

    private boolean tieneEtiqueta(View view, int fieldId) {
        if (view instanceof TextView && view.getLabelFor() == fieldId
                && ((TextView) view).getText().length() > 0) return true;
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                if (tieneEtiqueta(group.getChildAt(i), fieldId)) return true;
            }
        }
        return false;
    }

    private void pulsarGuardar() {
        onView(isRoot()).perform(closeSoftKeyboard());
        onView(withId(R.id.btnGuardarPerfil)).perform(scrollTo(), new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isAssignableFrom(View.class); }
            @Override public String getDescription() { return "Esperar boton visible tras scroll"; }
            @Override public void perform(UiController controller, View view) {
                long deadline = SystemClock.uptimeMillis() + 5000;
                do {
                    if (isDisplayingAtLeast(90).matches(view)) return;
                    controller.loopMainThreadForAtLeast(20);
                } while (SystemClock.uptimeMillis() < deadline);
                throw new AssertionError("Guardar no es alcanzable despues del scroll");
            }
        }, click());
    }

    private void abrirPerfilNuevo() {
        scenario = ActivityScenario.launch(PerfilActivity.class);
        esperarEstado(R.string.perfil_nuevo);
    }

    private void completarFormulario() {
        scenario.onActivity(activity -> {
            int[] ids = {R.id.etEdad, R.id.etPesoActual, R.id.etAltura,
                    R.id.etPesoObjetivo, R.id.etDiasEntrenamiento};
            String[] values = {"30", "78,00", "175", "82.00", "2"};
            for (int i = 0; i < ids.length; i++) ((EditText) activity.findViewById(ids[i])).setText(values[i]);
            for (int id : new int[] {R.id.spSexo, R.id.spObjetivo, R.id.spActividad, R.id.spPreferencia}) {
                ((Spinner) activity.findViewById(id)).setSelection(1);
            }
        });
    }

    private void esperarEstado(int stringId) {
        onView(isRoot()).perform(new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isRoot(); }
            @Override public String getDescription() { return "Esperar el estado de Perfil " + stringId; }
            @Override public void perform(UiController controller, View root) {
                long deadline = SystemClock.uptimeMillis() + 10000;
                String expected = root.getContext().getString(stringId);
                do {
                    TextView estado = root.findViewById(R.id.tvEstadoPerfil);
                    if (estado != null && expected.contentEquals(estado.getText())) return;
                    controller.loopMainThreadForAtLeast(20);
                } while (SystemClock.uptimeMillis() < deadline);
                throw new AssertionError("No se alcanzo el estado: " + expected);
            }
        });
    }
}
