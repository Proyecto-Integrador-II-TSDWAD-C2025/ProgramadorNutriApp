package com.example.nutriappmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutriappmovil.model.LoginRequest;
import com.example.nutriappmovil.model.LoginResponse;
import com.example.nutriappmovil.model.Usuario;
import com.example.nutriappmovil.network.ApiClient;
import com.example.nutriappmovil.network.ApiService;
import com.example.nutriappmovil.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnGoToRegister;
    private ProgressBar progressBarLogin;

    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        // Inicializar SessionManager y Retrofit con las clases reales del proyecto
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient(this).create(ApiService.class);

        btnLogin.setOnClickListener(v -> performLogin());

        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // TAREA: "Reapertura" - Verificar si ya hay sesión activa al abrir la app
        checkExistingSession();
    }

    private void checkExistingSession() {
        String token = sessionManager.getToken();
        if (token != null && !token.isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completá todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mostrarCarga(true);

        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Guardar el token de forma segura en SessionManager
                    String token = response.body().getToken();
                    sessionManager.saveToken(token);

                    // Validar sesión con /me y navegar
                    validarSesionNavegar();
                } else {
                    mostrarCarga(false);
                    Toast.makeText(LoginActivity.this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                mostrarCarga(false);
                Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validarSesionNavegar() {
        apiService.me().enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                mostrarCarga(false);
                if (response.isSuccessful() && response.body() != null) {
                    // Navegar al Dashboard bloqueando el botón "Atrás"
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    sessionManager.clearSession();
                    Toast.makeText(LoginActivity.this, "Error al obtener perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                mostrarCarga(false);
                Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarCarga(boolean isLoading) {
        if (progressBarLogin != null) {
            progressBarLogin.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        btnLogin.setEnabled(!isLoading);
        etEmail.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
    }
}