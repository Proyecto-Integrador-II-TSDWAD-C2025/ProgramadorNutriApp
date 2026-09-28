package com.example.nutriappmovil.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class SessionManager {

    private static final String PREF_NAME = "nutriapp_session";
    private static final String LEGACY_KEY_TOKEN = "auth_token";
    private static final String KEY_ENCRYPTED_TOKEN = "encrypted_auth_token";
    private static final String KEY_TOKEN_IV = "auth_token_iv";
    private static final String KEY_ALIAS = "nutriapp_auth_token_key";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    public void saveToken(String token) {
        String normalizedToken = SessionToken.normalize(token);

        if (normalizedToken == null) {
            clearSession();
            return;
        }

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey());

            String encryptedToken = Base64.encodeToString(
                    cipher.doFinal(normalizedToken.getBytes(java.nio.charset.StandardCharsets.UTF_8)),
                    Base64.NO_WRAP
            );
            String initializationVector = Base64.encodeToString(
                    cipher.getIV(),
                    Base64.NO_WRAP
            );

            preferences.edit()
                    .putString(KEY_ENCRYPTED_TOKEN, encryptedToken)
                    .putString(KEY_TOKEN_IV, initializationVector)
                    .remove(LEGACY_KEY_TOKEN)
                    .apply();
        } catch (GeneralSecurityException exception) {
            clearSession();
            throw new IllegalStateException("No se pudo proteger el token de sesión", exception);
        }
    }

    public String getToken() {
        String encryptedToken = preferences.getString(KEY_ENCRYPTED_TOKEN, null);
        String initializationVector = preferences.getString(KEY_TOKEN_IV, null);

        if (encryptedToken != null && initializationVector != null) {
            try {
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(
                        Cipher.DECRYPT_MODE,
                        getOrCreateSecretKey(),
                        new GCMParameterSpec(
                                128,
                                Base64.decode(initializationVector, Base64.NO_WRAP)
                        )
                );

                byte[] decryptedToken = cipher.doFinal(
                        Base64.decode(encryptedToken, Base64.NO_WRAP)
                );
                return SessionToken.normalize(
                        new String(decryptedToken, java.nio.charset.StandardCharsets.UTF_8)
                );
            } catch (GeneralSecurityException | IllegalArgumentException exception) {
                clearSession();
                return null;
            }
        }

        return migrateLegacyToken();
    }

    public boolean isLoggedIn() {
        return SessionToken.normalize(getToken()) != null;
    }

    public void clearSession() {
        preferences.edit()
                .remove(KEY_ENCRYPTED_TOKEN)
                .remove(KEY_TOKEN_IV)
                .remove(LEGACY_KEY_TOKEN)
                .apply();
    }

    private String migrateLegacyToken() {
        String legacyToken = SessionToken.normalize(
                preferences.getString(LEGACY_KEY_TOKEN, null)
        );

        if (legacyToken != null) {
            saveToken(legacyToken);
        }

        return legacyToken;
    }

    private SecretKey getOrCreateSecretKey() throws GeneralSecurityException {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        try {
            keyStore.load(null);
        } catch (java.io.IOException exception) {
            throw new GeneralSecurityException("No se pudo abrir Android Keystore", exception);
        }

        SecretKey existingKey = (SecretKey) keyStore.getKey(KEY_ALIAS, null);
        if (existingKey != null) {
            return existingKey;
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEY_STORE
        );
        keyGenerator.init(
                new KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
                )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build()
        );
        return keyGenerator.generateKey();
    }
}
