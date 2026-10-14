package com.example.nutriappmovil.network;

import com.example.nutriappmovil.model.LoginRequest;
import com.example.nutriappmovil.model.LoginResponse;
import com.example.nutriappmovil.model.Usuario;
import com.example.nutriappmovil.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("login/")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("me/")
    Call<Usuario> me();

    @POST("logout/")
    Call<Void> logout();

    @POST("register/")
    Call<Usuario> register(@Body RegisterRequest request);
}