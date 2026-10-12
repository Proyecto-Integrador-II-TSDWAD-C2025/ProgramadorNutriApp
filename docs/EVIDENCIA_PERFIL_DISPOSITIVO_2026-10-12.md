# Evidencia de pruebas de Perfil en dispositivo

- Fecha: 12/10/2026, aproximadamente 19:04, Argentina (UTC-03:00).
- Dispositivo: Samsung SM-A217M, Android 12, API 31.
- Rama: `fix/pablo-perfil-android`, PR #62 (abierto durante las pruebas).
- Commit probado: `05ba3a8923f0f26bbc3ac9d7838b057288209b05`.
- APK: debug, aplicación `com.example.nutriappmovil`, versión 1.0 (1).
- SHA-256 APK: `E26B650E5307542D632ACEE94087352E5505C772974F7FAC435B9DDC6C762FE9`.
- Ejecutor: AndroidJUnitRunner, clase `PerfilActivityTest`.

## Resultado

| Caso | Resultado |
|---|---|
| `dobleClickNoDuplicaPutYLaRotacionConservaEdicion` | PASS |
| `falloDeGuardadoConservaDatosYPermiteReintentar` | PASS |
| `validacionLocalNoEnviaValoresFueraDeRango` | PASS |
| `sesionInvalidaBloqueaEdicionSinCambiarLogin` | PASS |
| `cargaFallidaBloqueaGuardadoYPermiteReintentar` | PASS |
| `muestraError400EnElCampoCorrespondiente` | PASS |

Salida final observada del runner:

```text
Time: 10.088

OK (6 tests)

INSTRUMENTATION_CODE: -1
```

Cada caso devolvió `INSTRUMENTATION_STATUS_CODE: 0` al finalizar.

Se repitió la clase antes de integrar el PR, aproximadamente a las 19:08:
**6/6 aprobadas** nuevamente, duración 8,836 s. Se conserva la
[salida íntegra del runner de esta segunda ejecución](PERFIL_ANDROIDJUNIT_2026-10-12.txt).
El archivo contiene la salida real de ADB, no una captura ni una reconstrucción.

## Ejecución y limitaciones

El intento inicial de `connectedDebugAndroidTest` se abortó y produjo un
reporte con cero casos. Ese reporte de Gradle **no es** el resultado aprobado.
Se instalaron manualmente el APK debug y el APK androidTest; para este último
se utilizó `adb install --no-streaming -r -t` después de detener el cliente
de instalación que seguía esperando. El runner se ejecutó directamente:

```powershell
adb shell am instrument -w -r -e class com.example.nutriappmovil.PerfilActivityTest com.example.nutriappmovil.test/androidx.test.runner.AndroidJUnitRunner
```

Las respuestas HTTP se interceptan en memoria. Esta ejecución no llama a
Railway ni valida MySQL, login real o aislamiento entre cuentas. Tampoco
valida TalkBack, contraste, fuente ampliada ni Appium.

La app de prueba quedó instalada. Durante las pruebas no se realizaron cambios
en las tareas de Fernando/Johana ni se mergeó el PR. Este registro resume la
salida observada por ADB; no es un XML generado por Gradle.

Tras el intento de Gradle, el paquete anterior ya no figuraba instalado.
La nueva instalación registra `firstInstallTime=2026-10-12 19:02:12`, frente
al 05/10/2026 de la instalación anterior. Por ello no se garantiza conservar
el estado local previo; no se tomó una copia de ese estado. Los datos remotos
no fueron modificados por las pruebas. Al terminar se abrió la app en Login.
