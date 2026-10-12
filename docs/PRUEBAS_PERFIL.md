# Perfil: implementación y pruebas — Sprint 2

Corte: 12/10/2026. Responsable: Pablo. Endpoints: `GET /api/perfil/` y
`PUT /api/perfil/`, con token gestionado por la infraestructura común.

## Comportamiento

- Carga correcta: mostrar los datos del servidor y habilitar edición.
- 404: habilitar el formulario para crear el primer perfil.
- Fallo de carga: bloquear edición/guardado y ofrecer reintento, para evitar
  sobrescribir un perfil que no pudo recuperarse.
- 401: informar que se necesita iniciar sesión; la pantalla no modifica el
  flujo de autenticación.
- Guardado en curso: deshabilitar campos y botón para impedir envíos duplicados.
- 400: mostrar los errores de Django en cada campo; conservar los datos ingresados.
- 500/red: conservar datos y permitir reintento. Un corte de red no permite
  saber si el servidor terminó el guardado; volver a abrir Perfil para comprobarlo.
- Éxito: confirmar solo si hay un perfil válido en la respuesta, actualizar
  la pantalla e informar si requiere revisión profesional.
- Salida/rotación: cancelar los callbacks de la Activity anterior. La rotación
  conserva los campos editados; no reemplazarlos con otra carga automática.

Validación local: edad 13–100, altura 100–250 cm, peso 30–300 kg con hasta dos
decimales, días 1–6 y todas las opciones seleccionadas. Se admite coma decimal,
pero no NaN, Infinity, exponentes ni sufijos de Java.

## Accesibilidad

Etiquetas asociadas mediante `labelFor`, unidades visibles, campos con altura
adaptable y mensajes de estado persistentes con región accesible `polite`.
Los datos de salud quedan fuera del autocompletado. Se mantienen los mismos IDs
de los controles existentes para no romper localizadores.

Prueba manual pendiente: TalkBack, orden de foco, anuncio de errores y mensajes,
fuente al 200 %, contraste y teclado numérico en un dispositivo Android.

## Ejecución

Desde la raíz del proyecto, con JDK y SDK Android configurados:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
.\gradlew.bat assembleRelease -PNUTRIAPP_RELEASE_API_BASE_URL=https://nutriapp-appmovil.up.railway.app/api/
```

Resultados locales: 31 pruebas unitarias aprobadas (24 existentes y 7 nuevas);
build debug, APK de instrumentación, lint y build release verificados.
El APK release sigue sin firma: no es todavía la entrega final.

Las seis pruebas nuevas de pantalla están en `PerfilActivityTest`. Interceptan
respuestas HTTP en memoria, sin llamar a Railway ni modificar su base:

1. Carga fallida, bloqueo y reintento.
2. 401 y bloqueo de edición.
3. Error 400 asociado al campo.
4. Fallo de guardado, conservación de datos y reintento.
5. Rotación y prevención de PUT duplicado.
6. Validación local sin enviar valores fuera de rango.

Con un dispositivo conectado o emulador iniciado:

```powershell
.\gradlew.bat connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.nutriappmovil.PerfilActivityTest
```

Ejecución en dispositivo realizada el 12/10/2026: Samsung SM-A217M,
Android 12 (API 31), commit `05ba3a8923f0f26bbc3ac9d7838b057288209b05`.
Resultado: **6/6 aprobadas**, `OK (6 tests)`, duración informada 10,088 s.
El primer intento con Gradle se abortó sin ejecutar casos; tras instalar
manualmente ambos APK, se ejecutó la clase con AndroidJUnitRunner por ADB.
[Evidencia y comando de ejecución](EVIDENCIA_PERFIL_DISPOSITIVO_2026-10-12.md).
No confundir estas pruebas con Appium, accesibilidad manual ni integración
contra MySQL: las respuestas HTTP están simuladas en memoria.

### Ampliación de accesibilidad (en revisión)

La rama `feature/PabloRomero` incluye dos comprobaciones estructurales y
ajustes acotados al layout de Perfil. La clase completa suma **8/8 PASS** en
el mismo Samsung; [registro y salida real](EVIDENCIA_ACCESIBILIDAD_PERFIL_2026-10-12.md).
Las seis pruebas anteriores siguen siendo el antecedente integrado por PR #62.
TalkBack y fuente ampliada no se dan por aprobados con estos tests.

## Regresión real pendiente

Cuando se integre el login real, usar una cuenta de pruebas: cargar, editar,
guardar, salir y volver a Perfil. Comprobar persistencia del peso e historial,
revisión profesional, campos inválidos, token inválido y pérdida de red.
Revisar con otra cuenta que el perfil siga aislado. Guardar capturas/resultados
con commit, APK, Android y dispositivo, sin tokens ni datos personales reales.

El backend incorpora el guardado transaccional en el PR #42 del repositorio web.
Su llegada a producción requiere configurar Railway y revisar las migraciones;
integrarlo a develop no equivale a desplegarlo.
