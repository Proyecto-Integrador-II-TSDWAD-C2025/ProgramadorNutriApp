package com.example.nutriappmovil.perfil;

import com.example.nutriappmovil.model.PerfilRequest;
import com.example.nutriappmovil.model.PerfilResponse;
import com.example.nutriappmovil.network.PerfilApiService;
import retrofit2.Call;

public final class RetrofitPerfilRepository implements PerfilRepository {
    private final PerfilApiService api;

    public RetrofitPerfilRepository(PerfilApiService api) {
        this.api = api;
    }

    @Override public Call<PerfilResponse> getPerfil() {
        return api.getPerfil();
    }

    @Override public Call<PerfilResponse> actualizarPerfil(PerfilRequest request) {
        return api.actualizarPerfil(request);
    }
}
