package com.example.nutriappmovil.network;

import com.example.nutriappmovil.model.PageResponse;
import com.example.nutriappmovil.model.Plan;
import com.example.nutriappmovil.model.PlanRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Servicio del dominio "planes" (PlanViewSet en Django). Mismo estilo que
 * PerfilApiService: las rutas van sin "api/" porque ya está en la base URL.
 * El token lo agrega AuthInterceptor.
 *
 * Permisos del backend: cualquier usuario autenticado puede leer (el rol
 * "usuario" solo ve planes activos); crear, editar y eliminar requieren
 * rol de staff (administrador / nutricionista). Los demás reciben 403.
 */
public interface PlanesApiService {

    // Listado paginado: page=1, 2, 3... Seguir mientras hasNext() sea true.
    @GET("planes/")
    Call<PageResponse<Plan>> listarPlanes(@Query("page") int page);

    @POST("planes/")
    Call<Plan> crearPlan(@Body PlanRequest request);

    @PUT("planes/{id}/")
    Call<Plan> actualizarPlan(@Path("id") int idPlan, @Body PlanRequest request);

    // Django responde 204 sin cuerpo
    @DELETE("planes/{id}/")
    Call<Void> eliminarPlan(@Path("id") int idPlan);
}