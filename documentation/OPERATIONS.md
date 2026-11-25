# Operación y auditoría

## Eventos mínimos

`LOGIN_SUCCESS/FAILURE`, `TOKEN_REFRESH/REVOKE`, `PASSWORD_CHANGED`, `INPUT_BLOCKED`, `PII_TOKENIZED`, `RAG_QUERY`, `NVD_CALL`, `MODEL_CALL`, `OUTPUT_BLOCKED`, `DOCUMENT_INDEXED/QUARANTINED/DUPLICATE`, `PROMPT_CHANGED`, `USER_CHANGED`, `AUDIT_READ`.

Cada evento lleva `eventId`, UTC, `correlationId`, actor, rol, acción, recurso, decisión, categorías y hashes. No lleva contraseña, JWT, API key, PII, prompt completo, chunk ni respuesta completa.

## Alertas

- ≥5 logins fallidos/usuario/15 min.
- ≥3 bloqueos de prompt injection/usuario/hora.
- cualquier canario/system prompt en salida.
- Presidio no disponible.
- cuarentena de documento.
- aumento de 429/5xx de NVD/OpenRouter.
- cambios de rol o prompt.

## Backup y recuperación

Backup diario cifrado de PostgreSQL y snapshot de Qdrant después de ingesta; pruebe recuperación trimestral. Mantenga copia de claves en KMS con versionado. RPO recomendado 24 h para RAG y 1 h para identidad/auditoría; RTO 4 h, ajustable.

## Rotación

Rote JWT trimestralmente y API keys según proveedor; acepte temporalmente clave actual/anterior. Rotar la clave de datos exige job de re-cifrado transaccional con versión de clave por fila.
