# NutriApp — Aplicación Móvil

**NutriApp** es una aplicación móvil nativa en Android que brinda planes personalizados de nutrición y entrenamiento físico, adaptados a las necesidades de cada usuario. Este repositorio contiene el cliente móvil (Java), desarrollado como parte del Proyecto Integrador II — Equipo TERACODE.

La plataforma tiene como objetivo principal ayudar a las personas a mejorar su salud y condición física, ya sea aumentando o disminuyendo su masa corporal, mediante recomendaciones específicas basadas en su perfil.

## Descripción

Este repositorio corresponde al cliente móvil nativo del ecosistema NutriApp, que también cuenta con un backend en Django REST Framework y un frontend web en Angular (repositorio [Programador-Web-TSDWAD-C2025](https://github.com/Proyecto-Integrador-II-TSDWAD-C2025/Programador-Web-TSDWAD-C2025)).

- **App Móvil**: Android nativo (Java)
- **Backend**: Django REST Framework con MySQL
- **API**: consumo de endpoints RESTful (a partir del Sprint 2)
- **Autenticación**: Token Authentication (DRF)

## Sprint 2 — estado al 12/10/2026

Android (Java/XML) → Retrofit/OkHttp por HTTPS → Django REST Framework → MySQL.
Perfil, Dashboard/Planes, Rutina y Alimentación tienen consumo de API integrado
en `develop`; esto no certifica su regresión completa ni su despliegue.
Login/sesión/logout (Fernando) y registro/CRUD de usuarios (Johana) siguen
pendientes de integración móvil. El token de DRF no es JWT ni expira a las 24 h.

- [Captura del reporte de pruebas de Perfil](evidencias/perfil-tests-2026-10-12.jpg)
- [Planilla y estado de Testing](../../wiki/Testing)

La documentación complementaria y los registros se entregan por separado en
PDF; se retiró la carpeta `docs` a pedido del equipo. Los originales siguen
recuperables en el historial de Git (commit `f7de775`).
La captura corresponde a un reporte basado en la salida real de ADB:
8/8 pruebas de Perfil, HTTP simulado. No acredita TalkBack, fuente ampliada,
Appium ni integración contra producción. Adjuntar a la planilla las capturas
de cada caso ejecutado, con commit/APK, dispositivo, fecha y resultado,
sin credenciales ni datos personales reales.

## Características (Sprint 1)

- Navegación completa entre 8 Activities
- Paleta de colores y tipografía institucional (Inter)
- Formulario de contacto funcional
- Ícono y branding propio integrado
- Estructura de ramas con Pull Requests

## Estructura del proyecto

```text
ProgramadorNutriApp/
├── app/
│ ├── src/main/java/com/example/nutriappmovil/ # Activities
│ ├── src/main/res/layout/ # Layouts XML
│ ├── src/main/res/values/ # colors.xml, strings.xml
│ ├── src/main/res/font/ # Tipografía Inter
│ └── src/main/res/drawable/ # Fondos y formas
└── README.md
```

## Instalación

### Requisitos previos

- Android Studio compatible con el Gradle Wrapper del proyecto
- JDK 17 y Android SDK 33 (compilación)
- Dispositivo Android con API 24+ o emulador

### Pasos

```bash
git clone -b develop https://github.com/Proyecto-Integrador-II-TSDWAD-C2025/ProgramadorNutriApp.git
```

1. Abrir Android Studio → **Open** → seleccionar la carpeta clonada.
2. Esperar la sincronización de Gradle.
3. Conectar un dispositivo Android por USB (con Depuración USB activada) o usar un emulador.
4. Ejecutar con el botón **Run** (▶).

### Configuración de API y pruebas

El debug utiliza `https://nutriapp-appmovil.up.railway.app/api/`, definido en
`app/build.gradle.kts`; `ApiConfig` lo obtiene de `BuildConfig.API_BASE_URL`.
No guardar tokens, contraseñas ni claves de firma en código o Git.

Desde la raíz, en PowerShell con JDK 17 y SDK configurados:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
```

Las pruebas unitarias suman 31 casos aprobados; la clase de Perfil tiene
8/8 pruebas aprobadas en Samsung SM-A217M/Android 12 (seis funcionales y
dos comprobaciones estructurales de accesibilidad), integradas por PR #64.
Usan HTTP simulado, no Railway. Con ambos APK instalados, ejecutar:

```powershell
adb shell am instrument -w -r -e class com.example.nutriappmovil.PerfilActivityTest com.example.nutriappmovil.test/androidx.test.runner.AndroidJUnitRunner
```

TalkBack, fuente ampliada y persistencia real requieren pruebas separadas.
Appium es una suite diferente: [instrucciones y dependencia de login](appium-tests/README.md).

### APK de release (preparación, no entrega final)

```powershell
.\gradlew.bat assembleRelease -PNUTRIAPP_RELEASE_API_BASE_URL=https://nutriapp-appmovil.up.railway.app/api/
```

Confirmar primero que esa URL corresponde al backend de producción aprobado.
No omitir la propiedad: su valor por defecto es `https://example.invalid/api/`.
El proyecto no configura firma release; este comando produce un APK **sin firmar**.
Para la entrega, usar Android Studio → Build → Generate Signed Bundle / APK → APK,
guardar la keystore fuera del repositorio y no compartirla con la evidencia.
Luego verificar URL, versión, instalación limpia, permisos y recorrido por rol.

## Estructura de ramas

- `main` — versión estable, entregada al cierre de cada Sprint.
- `develop` — rama de integración activa del equipo.
- `feature/nombre-apellido` — rama individual de cada integrante.

Todo cambio se integra a `develop` mediante Pull Request (nunca push directo). Al cierre de cada Sprint, `develop` se mergea a `main`.

Antes de integrar: revisar diff/conflictos, compilar y registrar pruebas del commit.
No sobrescribir ramas de compañeros ni cerrar issues solo porque compila.
La publicación en `main`, el deploy, la firma del APK y el video son pasos separados.

## Pantallas (Activities) — Sprint 1

Alcance del Sprint 1: navegabilidad entre pantallas, sin conexión al backend.

- **SplashActivity** — pantalla de bienvenida.
- **LoginActivity** — inicio de sesión.
- **RegisterActivity** — registro de nuevos usuarios.
- **DashboardActivity** — panel principal con accesos a las demás secciones.
- **MiRutinaActivity** — rutina de entrenamiento del usuario.
- **MiPlanAlimenticioActivity** — plan alimenticio del día.
- **PerfilActivity** — información personal del usuario.
- **ContactoActivity** — formulario de contacto.

## Mapa de navegación

```text
SplashActivity → LoginActivity → DashboardActivity ┬→ MiRutinaActivity
│ ├→ MiPlanAlimenticioActivity
└→ RegisterActivity ├→ PerfilActivity
└→ ContactoActivity
```

## Video de demostración

Video breve mostrando la navegación entre las 8 Activities del Sprint 1:
https://drive.google.com/file/d/1sPxGSkXx3gr8MmMe8ypwPvnBfQfO5mfR/view

Cada Activity hija permite volver a su Activity padre correspondiente.

## Próximos pasos

- [x] Infraestructura Retrofit/OkHttp y API por HTTPS
- [x] Consumo de Perfil, Planes, Mi Rutina y Mi Plan Alimenticio integrado
- [x] Almacenamiento cifrado del token disponible para integrar autenticación
- [x] Tests unitarios y ocho pruebas de Perfil aprobadas en dispositivo
- [ ] Implementar autenticación real con Token Authentication (DRF)
- [ ] Integrar registro y CRUD móvil de usuarios
- [ ] Ejecutar pruebas de pantalla de los demás módulos, TalkBack, Appium y regresión real por rol
- [ ] Generar el release (APK) para descarga

El código integrado requiere validación en dispositivo; no equivale a una
entrega final aprobada. La autenticación y el registro mantienen sus responsables.
Los detalles de implementación y los registros completos de Perfil se entregan
en los PDF complementarios. La captura enlazada arriba conserva el resultado
y sus limitaciones; los fallos históricos no se ocultan.

## Documentación del proyecto

- [Wiki del repositorio](../../wiki)
- Especificación de Requisitos de Software (IEEE 830) — documentos externos de la Wiki; actualización pendiente de revisar
- Plan de Pruebas y Test Cases — ver página [Testing](../../wiki/Testing)
- Plan de Seguridad (Ciberseguridad) — ver Wiki

## Equipo TERACODE

| Integrante | Rol | Responsabilidad (Sprint 1) |
|---|---|---|
| Fernandez, Ariel | Developer / Administrador del repositorio | Splash, Contacto, Mi Plan Alimenticio |
| Andreoli, Fernando | Developer | Login |
| Acosta, Johana | Developer | Registro |
| Rodas, Ma. Catalina | Developer | Dashboard |
| Nieto, Tomás | Developer | Mi Rutina |
| Romero, Pablo | Scrum Master | Perfil |

---

**Institución**: Instituto Superior Politécnico Córdoba (ISPC) — Tecnicatura Superior en Desarrollo Web y Aplicaciones Digitales — Proyecto Integrador II

**Última actualización**: 12/10/2026
