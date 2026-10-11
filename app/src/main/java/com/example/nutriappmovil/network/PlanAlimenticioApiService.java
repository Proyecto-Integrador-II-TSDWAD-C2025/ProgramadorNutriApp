package com.example.nutriappmovil.network;

import com.example.nutriappmovil.model.CompletarComidaRequest;
import com.example.nutriappmovil.model.CompletarComidaResponse;
import com.example.nutriappmovil.model.MiPlanAlimenticioResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Servicio del plan alimenticio del usuario logueado. Mismo estilo que
 * PerfilApiService: las rutas van sin "api/" porque ya está en la base URL.
 * El token lo agrega AuthInterceptor.
 *
 * Respuestas de error del backend (todas con {"detail": "..."}):
 * 401 sin token o token inválido; 404 sin perfil (GET) o sin plan activo (POST);
 * 400 si la comida no pertenece al plan del usuario.
 */
public interface PlanAlimenticioApiService {

    @GET("mi-plan-alimenticio/")
    Call<MiPlanAlimenticioResponse> getMiPlan();

    // Funciona como alternador: si la comida ya estaba completada hoy, la desmarca.
    // No enviar dos veces seguidas la misma solicitud.
    @POST("mi-plan-alimenticio/completar-comida/")
    Call<CompletarComidaResponse> completarComida(
            @Body CompletarComidaRequest request
    );
}
