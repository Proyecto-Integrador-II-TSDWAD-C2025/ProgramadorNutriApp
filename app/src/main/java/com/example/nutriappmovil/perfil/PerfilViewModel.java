package com.example.nutriappmovil.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutriappmovil.model.PerfilResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Retiene la carga entre recreaciones y permite probarla sin Android ni red real. */
public final class PerfilViewModel extends ViewModel {
    private final PerfilRepository repository;
    private final MutableLiveData<EstadoCargaPerfil> estado = new MutableLiveData<>();
    private Call<PerfilResponse> carga;

    public PerfilViewModel(PerfilRepository repository) {
        this.repository = repository;
    }

    public LiveData<EstadoCargaPerfil> getEstado() { return estado; }

    public void cargarPerfil() {
        if (estado.getValue() != null
                && estado.getValue().getTipo() == EstadoCargaPerfil.Tipo.CARGANDO) return;
        estado.setValue(new EstadoCargaPerfil(EstadoCargaPerfil.Tipo.CARGANDO, null));
        carga = repository.getPerfil();
        carga.enqueue(new Callback<PerfilResponse>() {
            @Override public void onResponse(Call<PerfilResponse> call, Response<PerfilResponse> response) {
                try {
                    if (call != carga || call.isCanceled()) return;
                    if (response.isSuccessful() && response.body() != null
                            && response.body().getPerfil() != null) {
                        estado.setValue(new EstadoCargaPerfil(EstadoCargaPerfil.Tipo.LISTO,
                                response.body().getPerfil()));
                    } else {
                        EstadoCargaPerfil.Tipo tipo = response.code() == 404
                                ? EstadoCargaPerfil.Tipo.NUEVO : response.code() == 401
                                ? EstadoCargaPerfil.Tipo.SESION_INVALIDA : EstadoCargaPerfil.Tipo.ERROR_HTTP;
                        estado.setValue(new EstadoCargaPerfil(tipo, null));
                    }
                } finally {
                    if (response.errorBody() != null) response.errorBody().close();
                }
            }

            @Override public void onFailure(Call<PerfilResponse> call, Throwable error) {
                if (call != carga || call.isCanceled()) return;
                estado.setValue(new EstadoCargaPerfil(EstadoCargaPerfil.Tipo.ERROR_CONEXION, null));
            }
        });
    }

    @Override protected void onCleared() {
        if (carga != null) carga.cancel();
    }

    public static final class Factory implements ViewModelProvider.Factory {
        private final PerfilRepository repository;

        public Factory(PerfilRepository repository) { this.repository = repository; }

        @Override @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass != PerfilViewModel.class) throw new IllegalArgumentException("ViewModel no soportado");
            return (T) new PerfilViewModel(repository);
        }
    }
}
