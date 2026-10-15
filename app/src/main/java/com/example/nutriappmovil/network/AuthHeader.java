package com.example.nutriappmovil.network;

import com.example.nutriappmovil.session.SessionToken;

public final class AuthHeader {

    private AuthHeader() {
    }

    public static String fromToken(String token) {
        String normalizedToken = SessionToken.normalize(token);
        return normalizedToken == null ? null : "Token " + normalizedToken;
    }
}
