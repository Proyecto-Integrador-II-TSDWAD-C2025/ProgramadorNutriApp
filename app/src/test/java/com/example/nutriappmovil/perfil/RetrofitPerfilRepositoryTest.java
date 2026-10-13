package com.example.nutriappmovil.perfil;

import com.example.nutriappmovil.model.PerfilRequest;
import com.example.nutriappmovil.model.PerfilResponse;
import com.example.nutriappmovil.network.PerfilApiService;
import com.google.gson.JsonParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static org.junit.Assert.*;

/** AUT-NET-01/02: HTTP sobre loopback; nunca se llama a Railway. */
public class RetrofitPerfilRepositoryTest {
    private MockWebServer server;
    private PerfilRepository repository;

    @Before public void preparar() throws Exception {
        server = new MockWebServer();
        server.start();
        PerfilApiService api = new Retrofit.Builder().baseUrl(server.url("/api/"))
                .addConverterFactory(GsonConverterFactory.create()).build().create(PerfilApiService.class);
        repository = new RetrofitPerfilRepository(api);
    }

    @After public void cerrar() throws Exception { server.shutdown(); }

    @Test public void respuesta200_convierteJsonEnPerfil() throws Exception {
        // Arrange
        server.enqueue(json(200, "{\"perfil\":{\"id_perfil\":7,\"edad\":30,\"peso_actual\":\"78.00\"},"
                + "\"requiere_revision\":false,\"mensaje\":\"Perfil de prueba\"}"));
        // Act
        Response<PerfilResponse> response = repository.getPerfil().execute();
        // Assert
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(7, response.body().getPerfil().getIdPerfil());
        assertEquals("78.00", response.body().getPerfil().getPesoActual());
        RecordedRequest request = server.takeRequest(2, TimeUnit.SECONDS);
        assertNotNull(request);
        assertEquals("GET", request.getMethod());
        assertEquals("/api/perfil/", request.getPath());
    }

    @Test public void respuesta500_devuelveErrorHttpSinRomperElRepositorio() throws Exception {
        // Arrange
        server.enqueue(json(500, "{\"detail\":\"Error simulado\"}"));
        // Act
        Response<PerfilResponse> response = repository.getPerfil().execute();
        // Assert
        assertFalse(response.isSuccessful());
        assertEquals(500, response.code());
        assertNull(response.body());
        assertNotNull(response.errorBody());
        response.errorBody().close();
    }

    @Test public void actualizarPerfil_enviaPutConContratoDeDjango() throws Exception {
        // Arrange
        server.enqueue(json(200, "{\"perfil\":{\"edad\":30}}"));
        PerfilRequest body = new PerfilRequest("m", 30, 78, 175, 82,
                "aumentar_masa", "bajo", "sin_preferencia", 2, "", "");
        // Act
        Response<PerfilResponse> response = repository.actualizarPerfil(body).execute();
        // Assert
        assertTrue(response.isSuccessful());
        RecordedRequest request = server.takeRequest(2, TimeUnit.SECONDS);
        assertNotNull(request);
        assertEquals("PUT", request.getMethod());
        assertEquals("/api/perfil/", request.getPath());
        assertEquals(175, JsonParser.parseString(request.getBody().readUtf8())
                .getAsJsonObject().get("altura_cm").getAsInt());
    }

    @Test public void respuesta400_conservaErroresPorCampo() throws Exception {
        // Arrange
        server.enqueue(json(400, "{\"edad\":[\"Dato invalido\"]}"));
        // Act
        Response<PerfilResponse> response = repository.getPerfil().execute();
        // Assert
        assertEquals(400, response.code());
        assertNotNull(response.errorBody());
        assertEquals("Dato invalido", ErroresPerfil.leer(response.errorBody().string()).get("edad"));
    }

    private MockResponse json(int code, String body) {
        return new MockResponse().setResponseCode(code).addHeader("Content-Type", "application/json").setBody(body);
    }
}
