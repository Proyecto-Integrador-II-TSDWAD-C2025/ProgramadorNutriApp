package com.example.nutriappmovil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutriappmovil.model.RegisterRequest;
import com.example.nutriappmovil.model.Usuario;
import com.example.nutriappmovil.network.ApiClient;
import com.example.nutriappmovil.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    // Botones
    private Button btnRegisterSubmit;
    private Button btnBackToLogin;

    // Campos de texto
    private EditText etEmail;
    private EditText etPassword;
    private EditText etNombre;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. Vincular los botones de tu diseño actual
        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // 2. Vincular los campos de texto con los IDs correctos del XML
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etNombre = findViewById(R.id.etRegisterName);

        // 3. Inicializar Retrofit
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // 4. Flujo para volver atrás
        btnBackToLogin.setOnClickListener(v -> finish());

        // 5. Flujo de registro REAL contra el servidor
        btnRegisterSubmit.setOnClickListener(v -> performRegister());
    }

    private void performRegister() {
        // Obtenemos el texto ingresado
        String email = etEmail != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword != null ? etPassword.getText().toString().trim() : "";
        String nombre = etNombre != null ? etNombre.getText().toString().trim() : "Usuario";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completá email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegisterSubmit.setEnabled(false); // Bloquear botón para no mandar doble click

        // Armamos la petición
        RegisterRequest request = new RegisterRequest(email, password, nombre, "Usuario");

        apiService.register(request).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                btnRegisterSubmit.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Cuenta creada. Ahora iniciá sesión.", Toast.LENGTH_LONG).show();

                    // Volvemos al Login para que inicie sesión y obtenga el Token
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Acá capturamos el mensaje exacto de error que manda Django
                    String detalleError = "Error " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            detalleError += ": " + response.errorBody().string();
                        }
                    } catch (Exception ignored) {}

                    Toast.makeText(RegisterActivity.this, detalleError, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                btnRegisterSubmit.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}