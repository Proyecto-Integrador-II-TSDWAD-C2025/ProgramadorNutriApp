package com.example.nutriappmovil.perfil;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.example.nutriappmovil.model.PerfilResponse;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** AUT-UNIT-02: estado LiveData y colaboraciones, con repositorio simulado. */
public class PerfilViewModelTest {
    @Rule public InstantTaskExecutorRule executor = new InstantTaskExecutorRule();
    @Mock private PerfilRepository repository;
    @Mock private Call<PerfilResponse> call;
    private AutoCloseable mocks;
    private PerfilViewModel viewModel;

    @Before public void preparar() {
        mocks = MockitoAnnotations.openMocks(this);
        when(repository.getPerfil()).thenReturn(call);
        viewModel = new PerfilViewModel(repository);
    }

    @After public void cerrar() throws Exception {
        viewModel.onCleared();
        mocks.close();
    }

    @Test public void respuestaValida_publicaPerfilListo() {
        // Arrange
        PerfilResponse body = new Gson().fromJson("{\"perfil\":{\"edad\":30}}", PerfilResponse.class);
        // Act
        viewModel.cargarPerfil();
        callback().onResponse(call, Response.success(body));
        // Assert
        assertEquals(EstadoCargaPerfil.Tipo.LISTO, viewModel.getEstado().getValue().getTipo());
        assertEquals(30, viewModel.getEstado().getValue().getPerfil().getEdad());
        verify(repository).getPerfil();
    }

    @Test public void respuesta500_publicaErrorSinPerfil() {
        // Arrange
        Response<PerfilResponse> response = Response.error(500,
                ResponseBody.create(MediaType.get("application/json"), "{\"detail\":\"Error de prueba\"}"));
        // Act
        viewModel.cargarPerfil();
        callback().onResponse(call, response);
        // Assert
        assertEquals(EstadoCargaPerfil.Tipo.ERROR_HTTP, viewModel.getEstado().getValue().getTipo());
        assertNull(viewModel.getEstado().getValue().getPerfil());
        verify(repository).getPerfil();
    }

    @Test public void falloDeConexion_publicaErrorRecuperable() {
        // Arrange
        IOException failure = new IOException("Red simulada no disponible");
        // Act
        viewModel.cargarPerfil();
        callback().onFailure(call, failure);
        // Assert
        assertEquals(EstadoCargaPerfil.Tipo.ERROR_CONEXION, viewModel.getEstado().getValue().getTipo());
        verify(repository).getPerfil();
    }

    @Test public void respuesta404_permiteCrearPerfil() {
        // Arrange
        Response<PerfilResponse> response = Response.error(404,
                ResponseBody.create(MediaType.get("application/json"), "{}"));
        // Act
        viewModel.cargarPerfil();
        callback().onResponse(call, response);
        // Assert
        assertEquals(EstadoCargaPerfil.Tipo.NUEVO, viewModel.getEstado().getValue().getTipo());
        assertNull(viewModel.getEstado().getValue().getPerfil());
    }

    @Test public void dobleCargaEnCurso_noDuplicaSolicitud() {
        // Arrange
        viewModel.cargarPerfil();
        // Act
        viewModel.cargarPerfil();
        // Assert
        assertEquals(EstadoCargaPerfil.Tipo.CARGANDO, viewModel.getEstado().getValue().getTipo());
        verify(repository, times(1)).getPerfil();
        verify(call, times(1)).enqueue(any());
    }

    @Test public void respuesta200SinPerfil_noConfirmaExitoFalso() {
        // Arrange
        PerfilResponse body = new Gson().fromJson("{}", PerfilResponse.class);
        // Act
        viewModel.cargarPerfil();
        callback().onResponse(call, Response.success(body));
        // Assert
        assertEquals(EstadoCargaPerfil.Tipo.ERROR_HTTP, viewModel.getEstado().getValue().getTipo());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Callback<PerfilResponse> callback() {
        ArgumentCaptor<Callback<PerfilResponse>> captor = ArgumentCaptor.forClass((Class) Callback.class);
        verify(call).enqueue(captor.capture());
        return captor.getValue();
    }
}
