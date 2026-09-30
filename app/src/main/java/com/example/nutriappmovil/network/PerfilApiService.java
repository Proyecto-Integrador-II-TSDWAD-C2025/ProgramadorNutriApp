package com.example.nutriappmovil.network;

import com.example.nutriappmovil.model.PerfilRequest;
import com.example.nutriappmovil.model.PerfilResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface PerfilApiService {

    @GET("perfil/")
    Call<PerfilResponse> getPerfil();

    @PUT("perfil/")
    Call<PerfilResponse> actualizarPerfil(
            @Body PerfilRequest request
    );
}