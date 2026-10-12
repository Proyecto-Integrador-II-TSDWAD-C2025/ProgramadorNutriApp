package com.example.nutriappmovil.perfil;

import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class ErroresPerfilTest {
    @Test public void extraeErroresPorCampoDeDRF() {
        Map<String, String> errores = ErroresPerfil.leer("{\"edad\":[\"Fuera de rango\"],\"peso_actual\":[\"Solo dos decimales\"],\"detail\":\"Revisar\"}");
        assertEquals("Fuera de rango", errores.get("edad"));
        assertEquals("Solo dos decimales", errores.get("peso_actual"));
        assertEquals("Revisar", errores.get("detail"));
    }

    @Test public void respuestasVaciasHTMLONoObjetoNoRompenElFormulario() {
        for (String cuerpo : new String[] {null, "", "<html>Error</html>", "[]", "null", "{bad"}) {
            assertTrue(ErroresPerfil.leer(cuerpo).isEmpty());
        }
    }

    @Test public void ignoraEstructurasNoCompatibles() {
        assertTrue(ErroresPerfil.leer("{\"edad\":[],\"peso_actual\":{},\"altura_cm\":null,\"ok\":true}").isEmpty());
    }
}
