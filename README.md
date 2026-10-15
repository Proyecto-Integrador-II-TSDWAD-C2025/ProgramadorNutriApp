# NutriApp - Equipo TERACODE

Aplicación Android para consultar y gestionar información nutricional y de
entrenamiento. Desarrollada para el Proyecto Integrador II del Instituto Superior
Politécnico Córdoba (ISPC), en la Tecnicatura Superior en Desarrollo Web y
Aplicaciones Digitales.

## Objetivo y alcance

NutriApp reúne el perfil personal, los planes alimenticios y las rutinas de
ejercicios en una interfaz móvil. El alcance del proyecto contempla:

- Registro, autenticación, sesión y acceso según el rol.
- Panel principal, perfil personal, alimentación, rutina y contacto.
- Gestión de usuarios y planes con permisos del servidor.
- Registro de comidas y ejercicios completados.
- Contenido multimedia de apoyo.

El cierre de la integración y la validación de la versión de entrega están
pendientes. El alcance describe el producto; no certifica la aprobación de
todos sus recorridos.

## Arquitectura y tecnologías

```text
Android (Java / XML)
        |
 API REST / HTTPS
        |
Django REST Framework
        |
      MySQL
```

El ecosistema incluye un cliente web Angular, disponible junto al backend en
[Programador-Web-TSDWAD-C2025](https://github.com/Proyecto-Integrador-II-TSDWAD-C2025/Programador-Web-TSDWAD-C2025).

El cliente móvil utiliza AndroidX/Material, Retrofit, OkHttp y Gson.
La API emplea Token Authentication de Django REST Framework.
La interfaz utiliza la identidad visual del proyecto y la tipografía Inter.

## Instalación y ejecución

Requisitos: Android Studio compatible con el Gradle Wrapper, JDK 17,
Android SDK 33 y un dispositivo o emulador con Android API 24 o superior.

```powershell
git clone -b develop https://github.com/Proyecto-Integrador-II-TSDWAD-C2025/ProgramadorNutriApp.git
```

1. Abrir el proyecto en Android Studio.
2. Configurar el SDK y esperar la sincronización de Gradle.
3. Conectar un dispositivo con depuración USB o iniciar un emulador.
4. Seleccionar el módulo `app` y ejecutar la aplicación.

La URL de desarrollo se define en `app/build.gradle.kts` y se expone mediante
`BuildConfig.API_BASE_URL`. El dispositivo necesita acceso a Internet y al
backend configurado.

## Estructura del repositorio

```text
app/
  src/main/java/com/example/nutriappmovil/
    adapter/     Adaptadores de interfaz
    data/        Soporte de datos
    model/       Modelos y contratos de datos
    multimedia/  Componentes multimedia
    network/     Servicios y configuración de API
    perfil/      Lógica de soporte del perfil
    session/     Gestión local de sesión
  src/main/res/      Layouts, estilos y recursos
  src/test/          Pruebas automatizadas
  src/androidTest/   Pruebas instrumentadas
appium-tests/        Automatización end-to-end
```

## Desarrollo y calidad

- `feature/<integrante>`: trabajo individual.
- `develop`: integración mediante Pull Requests.
- `main`: versión estable aprobada para la entrega.

Las revisiones contemplan funcionamiento, validaciones, errores de red,
permisos, accesibilidad y protección de datos. Los casos, resultados y
capturas se registran en la planilla de Testing.

## Distribución y seguridad

El APK de entrega debe estar firmado y utilizar la URL HTTPS del backend
aprobado. Su publicación y la validación del despliegue están pendientes.

Para preparar la compilación, sustituir el dominio por el del entorno aprobado:

```powershell
.\gradlew.bat assembleRelease -PNUTRIAPP_RELEASE_API_BASE_URL=https://dominio-aprobado/api/
```

La compilación no sustituye la configuración de firma. No distribuir un APK
sin firmar ni usar la URL de ejemplo incluida por defecto.
Credenciales, tokens, archivos `.env` y claves de firma deben permanecer
fuera del repositorio y de las evidencias.

## Documentación y entrega

La [Wiki del proyecto](https://github.com/Proyecto-Integrador-II-TSDWAD-C2025/ProgramadorNutriApp/wiki) reúne la documentación general.
El [índice de documentación y pendientes](https://github.com/Proyecto-Integrador-II-TSDWAD-C2025/ProgramadorNutriApp/wiki/Documentación-y-entrega)
identifica los documentos disponibles y los entregables por completar.

- [ ] Actualizar y validar la especificación de requisitos IEEE 830.
- [ ] Publicar el plan de pruebas final y cerrar la planilla con capturas.
- [ ] Completar los informes de accesibilidad y automatización Appium.
- [ ] Actualizar el plan de ciberseguridad para la versión de entrega.
- [ ] Completar actas y trazabilidad del Sprint 2.
- [ ] Publicar guía de despliegue, APK firmado y video final.

Los documentos pendientes no se consideran entregados ni aprobados.

## Equipo TERACODE

Ariel Fernandez, Fernando Andreoli, Johana Vanessa Acosta,
Ma. Catalina Rodas, Tomás Nieto y Pablo Romero.

Última actualización documental: 12/10/2026.
