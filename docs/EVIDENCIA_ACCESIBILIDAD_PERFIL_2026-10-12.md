# Evidencia: ajustes y comprobaciones de accesibilidad de Perfil

- Fecha: 12/10/2026, Argentina (UTC-03:00).
- Dispositivo: Samsung SM-A217M, Android 12/API 31.
- Código probado: `644d9a73ff76d6c814fb660fc2a710bfda745e23`.
- Rama: `docs/pablo-cierre-sprint2`, basada en develop `545f224`.
- APK debug SHA-256: `893B50ADC70D61D5E56A84B6B7790A0AA74F5A16BF65E87AE89028DF62C919F8`.

## Resultado observado

**8/8 PASS**, `OK (8 tests)`, 11,936 s. Se conservan los seis casos
funcionales previos y se añaden:

1. `etiquetasEncabezadoYAlturaAdaptableEstanConfigurados`: asociación de las
   11 etiquetas, encabezado accesible, región de estado y altura de Volver.
2. `guardarMantieneContrasteNormalYPresionado`: colores efectivos del botón
   y contraste mínimo conservador 4,5:1, sin enviar PUT.

[Salida real de la ejecución aprobada](PERFIL_ACCESIBILIDAD_OK_2026-10-12.txt).
La repetición de confirmación también dio **8/8 PASS**, 11,439 s:
[salida real de la segunda ejecución](PERFIL_ACCESIBILIDAD_REPETICION_2026-10-12.txt).

El [primer intento](PERFIL_ACCESIBILIDAD_INTENTO1_2026-10-12.txt) dio 7/8 PASS:
el clic del caso 400 no cumplió la restricción de visibilidad del botón después
del scroll. No se descartó ese resultado. Se corrigió la sincronización del test:
cierra el teclado y espera visibilidad >= 90 % antes del clic; conserva las
aserciones y falla si el botón no se alcanza. No se desactivaron animaciones
ni se modificaron ajustes globales del teléfono.

Compilación debug, APK de tests, 31 unitarias y lint aprobados. Lint mantiene
169 advertencias generales, sin errores ni hallazgos en los recursos de Perfil
modificados. Backend repetido por separado: 40/40 PASS en SQLite aislado;
`check` sin incidencias. Nada de esto certifica producción/MySQL.

## Reproducción y alcance

Con ambos APK instalados (actualización `adb install --no-streaming -r`;
añadir `-t` para androidTest):

```powershell
adb shell am instrument -w -r -e class com.example.nutriappmovil.PerfilActivityTest com.example.nutriappmovil.test/androidx.test.runner.AndroidJUnitRunner
```

Se actualizó la instalación existente, sin desinstalar ni limpiar datos; la
fecha de primera instalación permaneció 12/10/2026 19:02:12.
HTTP simulado en memoria, sin Railway. TalkBack, fuente ampliada, navegación
con lector y revisión visual continúan **pendientes de ejecución manual**.
Ver [checklist manual](ACCESIBILIDAD_PERFIL.md). No confundir estas comprobaciones
con una auditoría completa de accesibilidad o Appium.
