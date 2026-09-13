package com.example.nutriappmovil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * DashboardActivity provisional para testing del Sprint 1.
 * Creado temporalmente para poder navegar a MiRutinaActivity.
 * TODO: Catalina Rodas reemplazara este archivo con el Dashboard definitivo.
 */
public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button btnMiRutina = findViewById(R.id.btnMiRutina);
        btnMiRutina.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MiRutinaActivity.class);
            startActivity(intent);
        });
    }
}
