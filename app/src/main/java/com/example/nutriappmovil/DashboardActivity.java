package com.example.nutriappmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.nutriappmovil.model.Usuario;
import com.example.nutriappmovil.network.ApiClient;
import com.example.nutriappmovil.network.ApiService;
import com.example.nutriappmovil.session.SessionManager;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * DashboardActivity — pantalla principal luego del login.
 * Sprint 2: muestra el nombre y el rol reales del usuario (GET /api/me/),
 * con estados de carga y error. Si el token es inválido o fue revocado (401),
 * limpia la sesión y vuelve al Login.
 * Desde acá se navega a las Activities hijas: Mi Plan Alimenticio,
 * Mi Rutina, Perfil y Contacto.
 */
public class DashboardActivity extends AppCompatActivity {

    private TextView tvSaludo;
    private TextView tvRol;
    private TextView tvErrorDashboard;
    private ProgressBar progressDashboard;
    private View layoutErrorDashboard;

    private SessionManager sessionManager;
    private ApiService apiService;
    private Call<Usuario> llamadaMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvSaludo = findViewById(R.id.tvSaludo);
        tvRol = findViewById(R.id.tvRol);
        tvErrorDashboard = findViewById(R.id.tvErrorDashboard);
        progressDashboard = findViewById(R.id.progressDashboard);
        layoutErrorDashboard = findViewById(R.id.layoutErrorDashboard);

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient(this).create(ApiService.class);

        ConstraintLayout cardMiPlanAlimenticio = findViewById(R.id.cardMiPlanAlimenticio);
        ConstraintLayout cardMiRutina = findViewById(R.id.cardMiRutina);
        ConstraintLayout cardPerfil = findViewById(R.id.cardPerfil);
        ConstraintLayout cardContacto = findViewById(R.id.cardContacto);
        View btnLogout = findViewById(R.id.btnLogout);
        View btnReintentar = findViewById(R.id.btnReintentar);

        cardMiPlanAlimenticio.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, MiPlanAlimenticioActivity.class)));

        cardMiRutina.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, MiRutinaActivity.class)));

        cardPerfil.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, PerfilActivity.class)));

        cardContacto.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ContactoActivity.class)));

        btnReintentar.setOnClickListener(v -> cargarUsuario());

        // Flujo de navegación para el Logout (el logout real contra el backend
        // lo implementa quien tiene a cargo login/logout)
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            // FLAG_ACTIVITY_NEW_TASK y FLAG_ACTIVITY_CLEAR_TASK borran las activities anteriores del historial
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        cargarUsuario();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (llamadaMe != null) {
            llamadaMe.cancel();
        }
    }

    /** Pide identidad y rol reales a /api/me/ y actualiza la pantalla. */
    private void cargarUsuario() {
        mostrarCargando();

        llamadaMe = apiService.me();
        llamadaMe.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (response.code() == 401) {
                    // Token inválido, vencido o revocado
                    volverAlLogin();
                } else if (!response.isSuccessful() || response.body() == null) {
                    mostrarError("No pudimos cargar tus datos (código " + response.code() + ").");
                } else {
                    mostrarUsuario(response.body());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) {
                    return;
                }
                mostrarError("No hay conexión con el servidor. Revisá tu internet e intentá de nuevo.");
            }
        });
    }

    private void mostrarCargando() {
        progressDashboard.setVisibility(View.VISIBLE);
        layoutErrorDashboard.setVisibility(View.GONE);
        tvRol.setText("Cargando tu perfil…");
    }

    private void mostrarUsuario(Usuario usuario) {
        progressDashboard.setVisibility(View.GONE);
        layoutErrorDashboard.setVisibility(View.GONE);

        String nombre = usuario.getNombre();
        tvSaludo.setText(nombre == null || nombre.isEmpty() ? "Hola 👋" : "Hola, " + nombre + " 👋");

        String rol = usuario.getRol() != null ? usuario.getRol().getNombreRol() : null;
        tvRol.setText("Rol: " + etiquetaRol(rol));
    }

    private void mostrarError(String mensaje) {
        progressDashboard.setVisibility(View.GONE);
        tvRol.setText("Perfil no disponible");
        tvErrorDashboard.setText(mensaje);
        layoutErrorDashboard.setVisibility(View.VISIBLE);
    }

    private void volverAlLogin() {
        sessionManager.clearSession();
        Toast.makeText(this, "Tu sesión expiró. Iniciá sesión de nuevo.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /** "administrador" -> "Administrador" */
    private String etiquetaRol(String rol) {
        if (rol == null || rol.isEmpty()) {
            return "sin rol";
        }
        return rol.substring(0, 1).toUpperCase(Locale.getDefault()) + rol.substring(1);
    }
}