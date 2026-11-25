# Modelo de amenazas y controles

## Principios

Zero trust entre fronteras, deny-by-default, defensa en profundidad, minimización, separación de funciones, trazabilidad, salidas estructuradas y fail closed para privacidad.

## Amenazas relevantes

| Amenaza | Prevención/detección | Respuesta |
|---|---|---|
| Prompt injection directa | normalización, patrones multilingües, prohibición de role-play/system prompt, orquestador determinista | 422 y `INPUT_BLOCKED` |
| Inyección indirecta en RAG | escaneo al ingerir y recuperar, cuarentena, delimitación de datos, sin herramientas dirigidas por LLM | omitir chunk y alertar |
| Exfiltración del system prompt | endpoint solo ADMIN, prompt nunca devuelto al chat, canario de fuga, filtro de salida | bloquear respuesta |
| PII hacia modelo | Presidio local, token único, restauración efímera, logs sanitizados | fail closed si Presidio falla |
| Discriminación | diccionario configurable de atributos protegidos + reglas contextuales | detener antes de RAG/NVD/modelo |
| Broken access control/IDOR | JWT firmado, `@PreAuthorize`, consulta por propietario, tests USER/AUDIT/ADMIN | 403 y auditoría |
| Archivo malicioso | límite, extensión/MIME, SHA-256, nombre servidor, Tika seguro, cuarentena; ClamAV recomendado | no indexar |
| Supply chain | versiones fijadas, Maven/lockfile, Dependabot/Renovate, SBOM, escaneo de imágenes | bloquear pipeline |
| Abuso/DoS | límites por usuario/IP, timeout, bulkhead, tamaño máximo y cuotas | 429/503 |
| Fuga por logs | MDC correlationId, allowlist de campos, enmascarado y sin bodies/tokens | alerta SIEM |
| JWT robado | access 15 min, refresh rotatorio hash, revocación, SameSite HttpOnly recomendado | revocar familia |
| Datos en reposo | AES-GCM app-level, volúmenes cifrados, KMS recomendado, backups cifrados | rotación y re-cifrado |

## Política de atributos protegidos

El requisito adopta una política estricta: consultas que intenten clasificar, priorizar, excluir o inferir personas por edad, sexo/género, orientación, nacionalidad, etnia, ubicación, religión, discapacidad o salud quedan bloqueadas. El mensaje al usuario no repite el contenido. Las reglas están en `application.yml` y deben ser revisadas por legal/ética; las regex no sustituyen un clasificador evaluado.

## Política de PII

1. El mensaje original se cifra para auditoría autorizada.
2. Presidio devuelve spans/tipo/puntuación.
3. Cada valor se sustituye por `<PII_TIPO_N>`; solo los tokens llegan a OpenRouter.
4. La respuesta se valida y los tokens conocidos se restauran. Tokens inventados no se restauran.
5. La auditoría registra tipos y cantidades, nunca valores.

## JWT y contraseñas

- Firma HMAC SHA-512 con secreto de al menos 512 bits (en producción, OIDC y claves asimétricas rotadas).
- Se valida `iss`, `aud`, `exp`, `nbf`, `jti` y roles conocidos.
- Access token de 15 minutos; refresh de 8 horas, rotatorio y almacenado como SHA-256.
- Contraseñas Argon2id, política de 14 caracteres, bloqueo progresivo y cambio obligatorio para semilla.
- Para browser productivo, entregue refresh token en cookie `HttpOnly; Secure; SameSite=Strict`; el ejemplo mantiene access token en memoria/sessionStorage por simplicidad educativa.

## Cabeceras y red

CSP sin `unsafe-eval`, `frame-ancestors 'none'`, HSTS tras habilitar TLS, `nosniff`, Referrer-Policy y Permissions-Policy. PostgreSQL, Qdrant y Presidio no se publican a Internet. Egress del backend debería permitirse solo hacia NVD y OpenRouter.

## Operación segura

- No use `.env` en producción; use Vault/KMS/secret manager.
- Envíe `audit_events` por outbox a almacenamiento WORM/SIEM.
- Defina retención (ejemplo: conversaciones 90 días, auditoría 365) y borrado verificable.
- Pruebe restauración de backups, rotación de claves e incident response.
- Mantenga revisión humana para decisiones críticas; NVD y el LLM pueden estar incompletos.
