package com.example.nutriappmovil;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.nutriappmovil.session.SessionManager;

/**
 * Qué hacer cuando el servidor responde 401 (token inválido, vencido o revocado):
 * limpiar la sesión y volver al Login, borrando el historial de pantallas.
 */
public final class SesionUtil {

    private SesionUtil() {
    }

    public static void volverAlLoginPorSesionExpirada(Activity activity) {
        new SessionManager(activity).clearSession();
        Toast.makeText(activity, "Tu sesión expiró. Iniciá sesión de nuevo.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}