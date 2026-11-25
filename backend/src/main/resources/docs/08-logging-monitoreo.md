# Logging y monitoreo seguro

**Código:** SEC-OPS-008 · **Versión:** 1.8

Registre autenticaciones, cambios de privilegio, acciones administrativas, decisiones de política, acceso a auditoría y llamadas a dependencias. Incluya UTC, correlationId, actor, acción, recurso, resultado y fuente. Evite cuerpos completos, PII, contraseñas, tokens, cookies y llaves.

Proteja integridad con envío centralizado, acceso separado, retención definida y alertas. Sincronice reloj. Una alerta debe tener dueño, umbral, playbook y prueba periódica. El fallo de logging crítico debe producir señal operativa, no filtrar datos en una excepción.
