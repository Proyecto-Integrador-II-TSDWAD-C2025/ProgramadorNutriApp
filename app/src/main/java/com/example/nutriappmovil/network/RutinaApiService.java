package com.example.nutriappmovil.network;

import com.example.nutriappmovil.model.CompletarEjercicioRequest;
import com.example.nutriappmovil.model.CompletarEjercicioResponse;
import com.example.nutriappmovil.model.MiRutinaResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RutinaApiService {

    @GET("mi-rutina/")
    Call<MiRutinaResponse> getMiRutina();

    @POST("mi-rutina/completar-ejercicio/")
    Call<CompletarEjercicioResponse> completarEjercicio(
            @Body CompletarEjercicioRequest request
    );
}
