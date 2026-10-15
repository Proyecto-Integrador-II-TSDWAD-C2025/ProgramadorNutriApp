package com.example.nutriappmovil.network;

import android.content.Context;

import com.example.nutriappmovil.session.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final SessionManager sessionManager;

    public AuthInterceptor(Context context) {
        sessionManager = new SessionManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request originalRequest = chain.request();

        String authorizationHeader = AuthHeader.fromToken(
                sessionManager.getToken()
        );

        if (authorizationHeader == null) {
            return chain.proceed(originalRequest);
        }

        Request authenticatedRequest = originalRequest
                .newBuilder()
                .header(
                        "Authorization",
                        authorizationHeader
                )
                .build();

        return chain.proceed(authenticatedRequest);
    }
}
