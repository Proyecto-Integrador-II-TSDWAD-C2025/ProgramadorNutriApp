# NutriApp — Aplicación Móvil

**NutriApp** es una aplicación móvil nativa en Android que brinda planes personalizados de nutrición y entrenamiento físico, adaptados a las necesidades de cada usuario. Este repositorio contiene el cliente móvil (Java), desarrollado como parte del Proyecto Integrador II — Equipo TERACODE.

La plataforma tiene como objetivo principal ayudar a las personas a mejorar su salud y condición física, ya sea aumentando o disminuyendo su masa corporal, mediante recomendaciones específicas basadas en su perfil.

## Descripción

Este repositorio corresponde al cliente móvil nativo del ecosistema NutriApp, que también cuenta con un backend en Django REST Framework y un frontend web en Angular (repositorio [Programador-Web-TSDWAD-C2025](https://github.com/Proyecto-Integrador-II-TSDWAD-C2025/Programador-Web-TSDWAD-C2025)).

- **App Móvil**: Android nativo (Java)
- **Backend**: Django REST Framework con MySQL
- **API**: consumo de endpoints RESTful (a partir del Sprint 2)
- **Autenticación**: Token Authentication (DRF)

## Características (Sprint 1)

- Navegación completa entre 8 Activities
- Paleta de colores y tipografía institucional (Inter)
- Formulario de contacto funcional
- Ícono y branding propio integrado
- Estructura de ramas con Pull Requests

## Estructura del proyecto

ProgramadorNutriApp/
├── app/
│ ├── src/main/java/com/example/nutriappmovil/ # Activities
│ ├── src/main/res/layout/ # Layouts XML
│ ├── src/main/res/values/ # colors.xml, strings.xml
│ ├── src/main/res/font/ # Tipografía Inter
│ └── src/main/res/drawable/ # Fondos y formas
└── README.md

## Instalación

### Requisitos previos

- Android Studio Giraffe | 2022.3.1
- JDK 17 (embebido en Giraffe)
- Dispositivo Android con API 24+ o emulador

### Pasos

```bash
git clone https://github.com/Proyecto-Integrador-II-TSDWAD-C2025/ProgramadorNutriApp.git
```

1. Abrir Android Studio Giraffe → **Open** → seleccionar la carpeta clonada.
2. Esperar la sincronización de Gradle.
3. Conectar un dispositivo Android por USB (con Depuración USB activada) o usar un emulador.
4. Ejecutar con el botón **Run** (▶).

## Estructura de ramas

- `main` — versión estable, entregada al cierre de cada Sprint.
- `develop` — rama de integración activa del equipo.
- `feature/nombre-apellido` — rama individual de cada integrante.

Todo cambio se integra a `develop` mediante Pull Request (nunca push directo). Al cierre de cada Sprint, `develop` se mergea a `main`.

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

SplashActivity → LoginActivity → DashboardActivity ┬→ MiRutinaActivity
│ ├→ MiPlanAlimenticioActivity
└→ RegisterActivity ├→ PerfilActivity
└→ ContactoActivity


Cada Activity hija permite volver a su Activity padre correspondiente.

## Próximos pasos

- [ ] Consumir los endpoints del backend Django (relevados en la Clase 03)
- [ ] Implementar autenticación real con Token Authentication (DRF)
- [ ] Persistir el token en SharedPreferences / DataStore
- [ ] Sumar tests automatizados
- [ ] Generar el release (APK) para descarga

## Documentación del proyecto

- [Wiki del repositorio](../../wiki)
- Especificación de Requisitos de Software (IEEE 830) — ver Wiki
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

**Última actualización**: Septiembre 2026