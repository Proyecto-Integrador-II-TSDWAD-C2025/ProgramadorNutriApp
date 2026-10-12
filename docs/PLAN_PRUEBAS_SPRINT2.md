# Plan de pruebas — Sprint 2 — TERACODE

Corte: 12/10/2026. Objetivo: validar Android → API HTTPS → MySQL,
los CRUD de usuarios y planes, roles, persistencia, errores y accesibilidad.
Estado: **EN PROGRESO**. Una compilación correcta no equivale a un caso PASS.

## Entornos y evidencia

- Android: JDK 17, SDK 33; mínimo API 24. Dispositivo utilizado: Samsung
  SM-A217M, Android 12/API 31. Repetir los recorridos en el APK final.
- Backend automatizado: SQLite en memoria, `proyecto.settings_test`; no Railway.
- Integración/Appium: backend y cuentas exclusivos de pruebas, con datos
  sintéticos. La suite Appium actual crea usuarios y perfiles; no ejecutarla
  contra producción sin acordar entorno y limpieza con el equipo.
- Por ejecución: ID, commit/APK, entorno, pasos, esperado, obtenido, PASS/FAIL/
  PENDIENTE, ejecutor, fecha y enlace a reporte/captura. Nunca incluir tokens,
  contraseñas, keystores ni datos reales de salud. La planilla externa conserva
  además prioridad, etiquetas, autor y comentarios.

## Qué ejecutar y quién valida

La tabla organiza el trabajo; no sustituye la planilla de casos existente.

| ID | Responsable | Recorrido y aceptación | Estado |
|---|---|---|---|
| S2-AUTH | Fernando | Login válido/inválido, sesión restaurada y logout que revoca token; Atrás no reabre sesión | Pendiente de integración |
| S2-USR | Johana | Registro válido/duplicado y CRUD usuarios con permisos; una cuenta no escala su rol | Pendiente de integración |
| S2-PER | Pablo | GET/PUT Perfil, persistencia al reabrir y aislamiento entre dos cuentas | Regresión real pendiente |
| S2-PER-UI | Pablo | Carga/reintento, 401, 400 por campo, 500, rotación, envío único y validaciones | 6/6 PASS con HTTP simulado |
| S2-PLAN | Catalina | Dashboard por rol y crear/listar/editar/eliminar planes; estados vacíos y errores | Regresión/evidencias pendientes |
| S2-RUT | Tomás | Leer rutina, completar ejercicio y comprobar persistencia al reabrir | Regresión/evidencias pendientes |
| S2-ALI | Ariel | Leer plan, completar comida y comprobar persistencia; multimedia y ciclo de vida | Regresión/evidencias pendientes |
| S2-RED | Cada responsable | Sin conexión, 401, 403, 400 y 500 sin crash, pérdida silenciosa ni falso éxito | Pendiente por módulo |
| S2-ACC | Cada responsable | TalkBack, foco, mensajes, fuente ampliada y controles alcanzables | Pendiente manual |
| S2-E2E | Tomás | Appium: login → rutina → completar → reabrir → persistencia | Pendiente; depende de login |
| S2-REL | Pablo + equipo | APK firmado, URL real, instalación limpia y smoke por rol | Pendiente |

## Resultados disponibles

- Backend: **40/40 PASS** en SQLite aislado (repetidos el 12/10/2026).
  Código: PR #42, commit `2f08a4825ca25211ea8ca7aeb747841a03e7e4af`.
  No certifica MySQL ni el deploy productivo.
- Android: **31/31 pruebas unitarias PASS**. Las 2 pruebas instrumentadas
  históricas de SessionManager se conservan como antecedente, sin nueva ejecución.
- Perfil: **6/6 pruebas funcionales instrumentadas PASS**, dos ejecuciones en
  dispositivo. [Registro](EVIDENCIA_PERFIL_DISPOSITIVO_2026-10-12.md) y
  [salida real](PERFIL_ANDROIDJUNIT_2026-10-12.txt), integradas por PR #62.
- [Accesibilidad de Perfil: revisión y pruebas adicionales](ACCESIBILIDAD_PERFIL.md).
- La Wiki conserva antecedentes de Perfil/MySQL/HTTPS; deben repetirse y
  enlazarse sobre la versión final. No convertir esos antecedentes en aprobación
  automática del release actual.

Comandos Android: ver [README](../README.md) y [Perfil](PRUEBAS_PERFIL.md).
Backend desde `back/` del repositorio web:

```powershell
python manage.py test api proyecto.test_settings --settings=proyecto.settings_test
python manage.py check --settings=proyecto.settings_test
```

## Cierre y trazabilidad

1. Cada integrante aporta resultados de su módulo y registra los fallos como issues.
2. Vincular requisitos/IEEE830 → historia/issue → PR/commit → caso → evidencia.
   Los documentos externos de IEEE830 aún requieren revisión; no se certifican aquí.
3. Completar accesibilidad y Appium después de integrar las dependencias.
4. No aprobar el sprint con recorridos obligatorios pendientes o fallos críticos.
5. Validar producción, APK firmado y video grupal de hasta 3 minutos; integrar
   la versión aprobada a `main` antes del 13/10/2026 a las 23:59.

Las ceremonias, asistentes y estimaciones se documentan solo con registros reales;
este plan no inventa reuniones ni cambia responsables de autenticación/usuarios.
