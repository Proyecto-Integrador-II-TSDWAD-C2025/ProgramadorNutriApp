package com.example.nutriappmovil.perfil;

import com.example.nutriappmovil.network.ApiClient;
import org.junit.rules.ExternalResource;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Aislamiento antes de lanzar la Activity; ninguna solicitud sale a Railway. */
public final class PerfilRedSimuladaRule extends ExternalResource {
    private Field clientField;
    private Object previousClient;
    private final AtomicInteger guardados = new AtomicInteger();

    @Override protected void before() throws Throwable {
        clientField = ApiClient.class.getDeclaredField("retrofit");
        clientField.setAccessible(true);
        previousClient = clientField.get(null);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            boolean saving = "PUT".equals(chain.request().method());
            if (saving) guardados.incrementAndGet();
            int code = saving ? 200 : 404;
            String body = saving ? "{\"perfil\":{\"sexo\":\"m\",\"edad\":30,"
                    + "\"peso_actual\":\"78.00\",\"altura_cm\":175,\"peso_objetivo\":\"82.00\","
                    + "\"objetivo\":\"aumentar_masa\",\"actividad\":\"bajo\","
                    + "\"preferencia\":\"sin_preferencia\",\"dias_entrenamiento\":2,"
                    + "\"limitaciones\":\"\",\"consideraciones_alimentarias\":\"\"},"
                    + "\"requiere_revision\":false}" : "{\"detail\":\"Sin perfil de prueba\"}";
            return new okhttp3.Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1)
                    .code(code).message("Respuesta simulada")
                    .body(ResponseBody.create(MediaType.get("application/json"), body)).build();
        }).build();
        clientField.set(null, new Retrofit.Builder().baseUrl("https://example.invalid/api/")
                .client(client).addConverterFactory(GsonConverterFactory.create()).build());
    }

    @Override protected void after() {
        try { clientField.set(null, previousClient); }
        catch (IllegalAccessException error) { throw new AssertionError(error); }
    }

    public int getGuardados() { return guardados.get(); }
}
