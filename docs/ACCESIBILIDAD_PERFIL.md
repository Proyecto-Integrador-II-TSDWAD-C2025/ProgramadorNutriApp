# Accesibilidad de Perfil — Sprint 2

Corte: 12/10/2026. Responsable: Pablo. Estado: revisión de código y pruebas
estructurales; **TalkBack y fuente ampliada todavía no aprobados**.

## Revisión y cambios acotados a Perfil

- Las 11 entradas tienen etiquetas `labelFor`; se explicitan años, kg y cm.
- Los controles tienen altura adaptable y mínimo de 48 dp o mayor.
  Se cambia «Volver» de altura fija a `wrap_content` con mínimo de 48 dp.
- El título queda identificado como encabezado accesible.
- Los estados usan una región `polite`; los errores no dependen solo del color.
- Se mantiene el autocompletado deshabilitado para datos de salud.
- «Guardar» utiliza un fondo exclusivo de Perfil, sin cambiar botones de otros
  módulos. El contraste calculado del texto blanco es 5,31:1 normal y 6,42:1
  presionado; el recurso anterior utilizaba 3,21:1 al presionar. Se aplica un
  objetivo conservador de 4,5:1 en ambos estados.
- Texto del formulario sobre blanco: 12,63:1; etiquetas: 7,24:1.
  Estos cálculos no certifican toda la app, modo oscuro ni estados deshabilitados.

Dos pruebas instrumentadas nuevas comprueban etiquetas, encabezado, región
de estado, altura adaptable y colores efectivos del botón. Sus resultados
de ejecución se registran por separado de los seis casos funcionales anteriores.
Las respuestas HTTP están simuladas; no se toca Railway.

Resultado en Samsung SM-A217M/Android 12: **8/8 PASS** (seis funcionales y
dos estructurales). Ver [registro y limitaciones](EVIDENCIA_ACCESIBILIDAD_PERFIL_2026-10-12.md).
Esta ampliación está en la rama `docs/pablo-cierre-sprint2`; no dar por integrado
el cambio a develop hasta que se apruebe su PR.

## Recorrido manual pendiente

| ID | Cómo comprobarlo | Aceptación | Estado |
|---|---|---|---|
| ACC-P01 | Activar TalkBack en Ajustes → Accesibilidad; recorrer Perfil con deslizamientos | Nombre, unidad y propósito comprensibles; sin IDs técnicos ni duplicación molesta | Pendiente |
| ACC-P02 | Recorrer spinners, campos, Volver, Reintentar y Guardar | Orden lógico; cada acción se alcanza y se activa | Pendiente |
| ACC-P03 | Con respuestas simuladas o cuenta de pruebas, provocar 400 y error de carga | Se anuncian errores/estado y se encuentra el campo afectado | Pendiente |
| ACC-P04 | Aumentar fuente al máximo disponible (objetivo 200 %), recorrer y abrir teclado | Sin texto cortado, solapamientos ni botones inaccesibles; scroll usable | Pendiente |
| ACC-P05 | Repetir en orientación horizontal y modo oscuro | Contenido y controles legibles, sin perder edición | Pendiente |
| ACC-P06 | Revisar controles y contraste con Accessibility Scanner, si está disponible | Registrar hallazgos y correcciones, no solo una captura sin contexto | Pendiente |

TalkBack cambia los gestos: tocar selecciona, doble toque activa y dos dedos
desplazan. Antes de activarlo, verificar cómo desactivarlo en ese dispositivo.
Al terminar, restaurar fuente/orientación y la configuración anterior.
No se modifican ajustes globales del teléfono automáticamente.

El login actual no autentica: sin sesión, Perfil puede mostrar 401 y bloquear
edición. Eso no basta para aprobar el recorrido del formulario editable;
completarlo con la integración real o un entorno de pruebas controlado.

Por cada caso registrar Android, APK/commit, ajuste utilizado, obtenido,
PASS/FAIL y evidencia. Las capturas complementan el reporte; el anuncio de
TalkBack requiere observación o grabación, no se acredita con una imagen estática.

Referencias: [guía Android](https://developer.android.com/guide/topics/ui/accessibility/apps)
y [pruebas de accesibilidad](https://developer.android.com/guide/topics/ui/accessibility/testing).
