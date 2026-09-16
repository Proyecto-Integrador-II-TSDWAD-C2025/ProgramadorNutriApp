package com.example.nutriappmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * DashboardActivity — pantalla principal luego del login.
 * Sprint 1: solo maqueta y navegación, sin conexión al backend todavía.
 * Desde acá se navega a las 4 Activities hijas: Mi Plan Alimenticio,
 * Mi Rutina, Perfil y Contacto.
 */
public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ConstraintLayout cardMiPlanAlimenticio = findViewById(R.id.cardMiPlanAlimenticio);
        ConstraintLayout cardMiRutina = findViewById(R.id.cardMiRutina);
        ConstraintLayout cardPerfil = findViewById(R.id.cardPerfil);
        ConstraintLayout cardContacto = findViewById(R.id.cardContacto);

        // Referencia al botón o ícono de cerrar sesión (asegurate de tener este ID en tu activity_dashboard.xml)
        View btnLogout = findViewById(R.id.btnLogout);

        cardMiPlanAlimenticio.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, MiPlanAlimenticioActivity.class)));

        cardMiRutina.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, MiRutinaActivity.class)));

        cardPerfil.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, PerfilActivity.class)));

        cardContacto.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ContactoActivity.class)));

        // Flujo de navegación para el Logout
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            // FLAG_ACTIVITY_NEW_TASK y FLAG_ACTIVITY_CLEAR_TASK borran las activities anteriores del historial
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}