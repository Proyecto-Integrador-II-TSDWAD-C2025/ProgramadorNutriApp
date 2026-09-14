package com.example.nutriappmovil;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Maqueta de Perfil para el Sprint 1.
 * Los campos todavía no se envían al backend.
 */
public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Button btnVolver = findViewById(R.id.btnVolverPerfil);
        Button btnGuardar = findViewById(R.id.btnGuardarPerfil);

        btnVolver.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> Toast.makeText(
                PerfilActivity.this,
                R.string.mensaje_perfil_demo,
                Toast.LENGTH_SHORT
        ).show());
    }
}