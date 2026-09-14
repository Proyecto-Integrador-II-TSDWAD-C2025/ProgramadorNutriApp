package com.example.nutriappmovil;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContactoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        findViewById(R.id.btnEnviarContacto).setOnClickListener(v -> {
            Toast.makeText(ContactoActivity.this,
                    "Mensaje enviado (funcionalidad pendiente)",
                    Toast.LENGTH_SHORT).show();
        });
    }
}