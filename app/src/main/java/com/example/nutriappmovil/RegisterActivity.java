
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

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegisterSubmit;
    private Button btnBackToLogin;

    private EditText etEmail;
    private EditText etPassword;
    private EditText etNombre;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etNombre = findViewById(R.id.etRegisterName);

        apiService = ApiClient.getClient(this)
                .create(ApiService.class);

        btnBackToLogin.setOnClickListener(v -> finish());

        btnRegisterSubmit.setOnClickListener(v -> performRegister());
    }

    private void performRegister() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();

        etEmail.setError(null);
        etPassword.setError(null);

        boolean hayErrores = false;

        if (email.isEmpty()) {
            etEmail.setError("Ingresá tu email");
            hayErrores = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email).matches()) {
            etEmail.setError("Ingresá un email válido");
            hayErrores = true;
        }

        if (password.isEmpty()) {
            etPassword.setError("Ingresá tu contraseña");
            hayErrores = true;
        } else if (password.length() < 8) {
            etPassword.setError(
                    "La contraseña debe tener al menos 8 caracteres"
            );
            hayErrores = true;
        }

        if (hayErrores) {
            return;
        }

        btnRegisterSubmit.setEnabled(false);

        RegisterRequest request =
                new RegisterRequest(email, password, nombre, "Usuario");

        apiService.register(request).enqueue(new Callback<Usuario>() {

            @Override
            public void onResponse(
                    Call<Usuario> call,
                    Response<Usuario> response
            ) {
                btnRegisterSubmit.setEnabled(true);

                if (response.isSuccessful()) {

                    Toast.makeText(
                            RegisterActivity.this,
                            "Cuenta creada. Ahora iniciá sesión.",
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(
                            RegisterActivity.this,
                            LoginActivity.class
                    );

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);
                    finish();

                } else {

                    try {
                        if (response.errorBody() != null) {

                            String errorJson =
                                    response.errorBody().string();

                            JSONObject errores =
                                    new JSONObject(errorJson);

                            if (errores.has("email")) {
                                mostrarErrorCampo(
                                        errores.get("email"),
                                        etEmail
                                );

                            } else if (errores.has("password")) {
                                mostrarErrorCampo(
                                        errores.get("password"),
                                        etPassword
                                );

                            } else if (errores.has("contrasena")) {
                                mostrarErrorCampo(
                                        errores.get("contrasena"),
                                        etPassword
                                );

                            } else {
                                Toast.makeText(
                                        RegisterActivity.this,
                                        errorJson,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }

                    } catch (Exception e) {
                        Toast.makeText(
                                RegisterActivity.this,
                                "No se pudo procesar el error del registro",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
            }

            @Override
            public void onFailure(
                    Call<Usuario> call,
                    Throwable t
            ) {
                btnRegisterSubmit.setEnabled(true);

                Toast.makeText(
                        RegisterActivity.this,
                        "Error de conexión",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void mostrarErrorCampo(Object error, EditText campo) {
        if (error instanceof JSONArray) {
            JSONArray mensajes = (JSONArray) error;

            if (mensajes.length() > 0) {
                campo.setError(mensajes.optString(0));
            }
        } else {
            campo.setError(error.toString());
        }
    }
}
