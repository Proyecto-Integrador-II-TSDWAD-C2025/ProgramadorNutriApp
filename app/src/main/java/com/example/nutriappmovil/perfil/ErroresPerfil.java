package com.example.nutriappmovil.perfil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Extrae errores DRF de campo; una respuesta vacia o no JSON usa el mensaje local. */
public final class ErroresPerfil {
    private ErroresPerfil() { }

    public static Map<String, String> leer(String cuerpo) {
        if (cuerpo == null || cuerpo.length() > 65536) {
            return Collections.emptyMap();
        }
        try {
            JsonElement root = JsonParser.parseString(cuerpo);
            if (!root.isJsonObject()) {
                return Collections.emptyMap();
            }
            Map<String, String> errores = new LinkedHashMap<>();
            JsonObject json = root.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                JsonElement valor = entry.getValue();
                if (valor.isJsonArray() && valor.getAsJsonArray().size() > 0) {
                    valor = valor.getAsJsonArray().get(0);
                }
                if (valor.isJsonPrimitive() && valor.getAsJsonPrimitive().isString()) {
                    String mensaje = valor.getAsString().trim();
                    if (!mensaje.isEmpty()) {
                        errores.put(entry.getKey(), mensaje.substring(0, Math.min(mensaje.length(), 500)));
                    }
                }
            }
            return errores;
        } catch (RuntimeException e) {
            return Collections.emptyMap();
        }
    }
}
