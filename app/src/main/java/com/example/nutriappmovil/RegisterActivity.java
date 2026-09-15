package com.example.nutriappmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegisterSubmit;
    private Button btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // Flujo al hacer clic en registrarse (simula registro exitoso y va al Login o Dashboard)
        btnRegisterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Al registrarse, podemos mandarlo al DashboardActivity para seguir el flujo
                Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish(); // Cerramos el registro para que no quede en la pila de atrás
            }
        });

        // Flujo para volver atrás (hija de LoginActivity)
        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simplemente finalizamos esta activity para volver a la pantalla padre (Login)
                finish();
            }
        });
    }
}