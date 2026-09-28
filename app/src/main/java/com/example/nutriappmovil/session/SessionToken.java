package com.example.nutriappmovil.session;

public final class SessionToken {

    private SessionToken() {
    }

    public static String normalize(String token) {
        if (token == null) {
            return null;
        }

        String normalizedToken = token.trim();
        return normalizedToken.isEmpty() ? null : normalizedToken;
    }
}
