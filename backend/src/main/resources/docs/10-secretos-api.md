# Manejo de secretos y API keys

**Código:** SEC-APP-010 · **Versión:** 1.3

Los secretos viven en un secret manager, no en código, imágenes, documentación, URLs o logs. Use identidad de workload cuando exista, privilegio mínimo, alcance y caducidad. Rote automáticamente y mantenga inventario de propietario y consumidores.

Si un secreto aparece en un repositorio o conversación: revóquelo primero, genere reemplazo, actualice consumidores, valide uso y elimine el dato de historial según procedimiento. Ocultarlo en un commit posterior no elimina la exposición. Detecte secretos en pre-commit y CI.
