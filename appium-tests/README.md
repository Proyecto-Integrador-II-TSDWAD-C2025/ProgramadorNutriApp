# NutriApp - Appium E2E Tests

Smoke tests automatizados para la app Android NutriApp usando **WebdriverIO + Appium**.

## Requisitos previos

- Node.js >= 18
- Appium Server instalado globalmente: `npm install -g appium`
- Android Emulator o dispositivo fisico con:
  - App debug instalada (`com.example.nutriappmovil`)
  - USB debugging habilitado (si es fisico)

## Instalacion

```bash
cd appium-tests
npm install
```

## Configuracion

Copiar `.env.example` a `.env` y ajustar:

```bash
cp .env.example .env
```

Variables:
- `API_BASE_URL`: URL del backend Django (por defecto ya apunta a Railway).
- `DEVICE_NAME`: nombre del emulador/dispositivo.
- `PLATFORM_VERSION`: version de Android.
- `APP_PATH` (opcional): ruta al APK si queres que Appium lo instale.

## Ejecucion

```bash
# Iniciar Appium server (en otra terminal)
appium

# Correr todos los tests
npm test

# Correr solo el smoke test de Mi Rutina
npm run test:smoke
```

## Estrategia de datos de prueba

El smoke test crea un usuario nuevo via API REST antes de cada ejecucion y le completa el perfil. Esto garantiza que siempre tenga una rutina asignada automaticamente por el backend.

## Dependencia pendiente

El login UI debe estar implementado en la app (`LoginActivity` real con llamada a backend y persistencia de token en `SessionManager`) para que este test funcione end-to-end.

## Localizadores estables

Los tests usan **Accessibility IDs** (`contentDescription`) definidos en la app:

| Elemento | Selector Appium |
|---|---|
| Email input | `~etEmail` |
| Password input | `~etPassword` |
| Boton Login | `~btnLogin` |
| Card Mi Rutina | `~cardMiRutina` |
| Titulo Mi Rutina | `~tvTitulo` |
| Ejercicio pendiente | `~Ejercicio: <nombre>, pendiente` |
| Ejercicio completado | `~Ejercicio: <nombre>, completado` |
| Boton Volver | `~btnVolver` |
