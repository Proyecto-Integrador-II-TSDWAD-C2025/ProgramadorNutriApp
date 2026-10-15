
package com.example.nutriappmovil.network;

import com.example.nutriappmovil.model.LoginRequest;
import com.example.nutriappmovil.model.LoginResponse;
import com.example.nutriappmovil.model.RegisterRequest;
import com.example.nutriappmovil.model.Usuario;

import java.util.List;
import com.example.nutriappmovil.model.EditarUsuarioRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import com.example.nutriappmovil.model.UsuariosResponse;
public interface ApiService {

    @POST("login/")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("me/")
    Call<Usuario> me();

    @POST("logout/")
    Call<Void> logout();

    @POST("register/")
    Call<Usuario> register(@Body RegisterRequest request);

    // Listar usuarios
    @GET("usuarios/")
    Call<UsuariosResponse> listarUsuarios();

    // Consultar un usuario por ID
    @GET("usuarios/{id}/")
    Call<Usuario> consultarUsuario(@Path("id") int id);

    // Editar un usuario
    @PUT("usuarios/{id}/")
    Call<Void> editarUsuario(
            @Path("id") int id,
            @Body EditarUsuarioRequest request
    );

    // Eliminar un usuario
    @DELETE("usuarios/{id}/")
    Call<Void> eliminarUsuario(@Path("id") int id);
}
