# Procedimiento de triaje de incidentes

**Código:** SEC-IR-001 · **Versión:** 1.2 · **Propietario:** SOC

## Objetivo
Clasificar un reporte de seguridad sin alterar evidencia. Registrar hora UTC, activo, reportante, síntoma, alcance conocido y canal de detección. No incluir contraseñas, tokens ni llaves en el ticket.

## Flujo
1. Asignar identificador y severidad provisional.
2. Confirmar si el evento continúa activo y si afecta confidencialidad, integridad o disponibilidad.
3. Preservar logs y hashes; trabajar sobre copias.
4. Para severidad crítica, avisar al líder de incidentes y activar contención aprobada.
5. Documentar hipótesis como hipótesis, no como hechos.

## Severidad
Crítica: explotación activa, credenciales privilegiadas o impacto amplio. Alta: compromiso confirmado de un activo importante. Media: indicios acotados sin explotación confirmada. Baja: evento informativo o control preventivo.

## Cierre
Exige causa, cronología, evidencias, acciones, propietario y fecha de seguimiento. La respuesta del asistente es apoyo; el responsable humano aprueba acciones disruptivas.
