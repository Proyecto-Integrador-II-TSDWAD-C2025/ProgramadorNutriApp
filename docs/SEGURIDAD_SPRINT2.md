# Seguridad — estado y pendientes del Sprint 2

Corte: 12/10/2026. Complemento técnico, no reemplazo de la evidencia de
Ciberseguridad del Sprint 1 ni certificación de cumplimiento.

## Código disponible

| Control | Implementación y límite |
|---|---|
| Transporte | Android bloquea HTTP claro; Retrofit usa URL HTTPS. Confirmar URL y proxy en producción. |
| Token local | `SessionManager` cifra con Android Keystore; reglas excluyen preferencias de sesión de backups. Login aún debe integrar este mecanismo. |
| Logs Android | OkHttp BASIC solo en debug; sin interceptor de logging en release. No adjuntar credenciales ni información de salud a evidencias. |
| Perfil | Edición bloqueada tras fallo de carga, validación local, errores por campo y sin guardados duplicados. |
| Backend | PR #42 en develop: configuración productiva explícita, cookies/HTTPS y guardado atómico de Perfil; pendiente verificar deploy y MySQL. |

## Advertencias que deben figurar en la documentación

- Se usa **TokenAuthentication de DRF**, no JWT. No existe expiración automática
  de 24 horas en el código actual; documentar el riesgo y acordar la política.
  Logout/revocación y recuperación de sesión requieren integración y prueba.
- Ocultar una opción en Android no basta: comprobar permisos en backend y
  aislamiento con cuentas de distintos roles. Perfil no admite elegir otro usuario.
- No copiar secretos del Sprint 1 o ejemplos de configuración: `.env`, tokens,
  contraseña de MySQL, claves administrativas y firma release quedan fuera de Git.
- `main` y `develop` del backend divergen. Revisar integración y migraciones;
  `0007_remove_legacy_seed_admin` elimina `admin@nutriapp.com`. Asegurar otra
  cuenta administrativa y respaldo antes de migrar; no ejecutar automáticamente.
- Producción: `DEBUG=False`, hosts explícitos y `TRUST_X_FORWARDED_PROTO=True`
  solo detrás del proxy confiable de Railway. No desactivar TLS/CSRF para evitar errores.
- Django sigue fijado en 4.2.11. La rama 4.2 terminó soporte el 07/04/2026:
  planificar actualización compatible y repetir tests; no cambiar solo el pin.
  [Calendario oficial](https://www.djangoproject.com/download/#supported-versions).
- Appium crea datos remotos y tiene configuración pendiente de revisar.
  No ejecutarlo contra producción ni habilitar permisos amplios por comodidad.
- El APK release actual no tiene firma configurada y usa una URL de ejemplo
  si se omite la propiedad de compilación. No distribuirlo como entrega final.

## Evidencia pendiente

Regresión por rol, aislamiento entre cuentas, token revocado, despliegue HTTPS
sin bucles, persistencia MySQL y APK firmado. Guardar solo datos sintéticos.
Ver [plan de pruebas](PLAN_PRUEBAS_SPRINT2.md) y
[configuración backend](https://github.com/Proyecto-Integrador-II-TSDWAD-C2025/Programador-Web-TSDWAD-C2025/blob/develop/back/README.md).
