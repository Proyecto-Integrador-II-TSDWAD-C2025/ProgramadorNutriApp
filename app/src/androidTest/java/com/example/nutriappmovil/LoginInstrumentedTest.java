package com.example.nutriappmovil;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoginInstrumentedTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void mostrarOcultarContrasena_funcionaCorrectamente() {
        // Escribe una contraseña de prueba
        onView(withId(R.id.etPassword))
                .perform(typeText("secreto123"), closeSoftKeyboard());

        // Toca el checkbox para mostrar la contraseña
        onView(withId(R.id.cbShowPassword)).perform(click());
        onView(withId(R.id.cbShowPassword)).check(matches(isChecked()));

        // Toca el checkbox para ocultarla nuevamente
        onView(withId(R.id.cbShowPassword)).perform(click());
        onView(withId(R.id.cbShowPassword)).check(matches(isNotChecked()));
    }
}