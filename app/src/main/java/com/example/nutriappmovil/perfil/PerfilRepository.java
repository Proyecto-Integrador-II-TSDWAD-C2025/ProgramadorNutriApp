package com.example.nutriappmovil.perfil;

import com.example.nutriappmovil.model.PerfilRequest;
import com.example.nutriappmovil.model.PerfilResponse;
import retrofit2.Call;

/** Contrato sustituible: la interfaz no conoce Activities ni credenciales. */
public interface PerfilRepository {
    Call<PerfilResponse> getPerfil();
    Call<PerfilResponse> actualizarPerfil(PerfilRequest request);
}
