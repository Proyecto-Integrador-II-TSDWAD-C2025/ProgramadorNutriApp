package com.example.nutriappmovil.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "nutriapp_session";
    private static final String KEY_TOKEN = "auth_token";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    public void saveToken(String token) {
        preferences.edit()
                .putString(KEY_TOKEN, token)
                .apply();
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void clearSession() {
        preferences.edit()
                .clear()
                .apply();
    }
}