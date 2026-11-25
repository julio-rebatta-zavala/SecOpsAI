# API REST

Base `/api/v1`. Errores: `application/problem+json` con `type`, `title`, `status`, `detail`, `instance`, `errorCode`, `correlationId`, `timestamp`.

| Método y ruta | Rol | Descripción |
|---|---|---|
| `POST /auth/login` | público | access+refresh JWT; bloqueo progresivo tras fallos |
| `POST /auth/refresh` | público | rota refresh token |
| `POST /auth/logout` | autenticado | revoca refresh |
| `PUT /me/password` | cualquiera | cambia contraseña y revoca sesiones |
| `POST /conversations` | USER, ADMIN | crea conversación |
| `GET /conversations` | USER, ADMIN | propias (ADMIN puede filtrar) |
| `GET /conversations/{id}` | propietario, ADMIN | detalle |
| `POST /conversations/{id}/messages` | USER, ADMIN | ejecuta pipeline seguro |
| `GET /audit/conversations` | AUDIT, ADMIN | todas, paginadas |
| `GET /audit/events` | AUDIT, ADMIN | eventos sanitizados |
| `GET /audit/application-logs` | AUDIT, ADMIN | vista derivada segura, no archivo arbitrario |
| `GET /admin/users` | ADMIN | usuarios |
| `POST /admin/users` | ADMIN | alta |
| `PATCH /admin/users/{id}` | ADMIN | roles/estado |
| `GET /admin/system-prompt` | ADMIN | versión activa |
| `PUT /admin/system-prompt` | ADMIN | crea versión y activa |
| `GET /admin/documents` | ADMIN | documentos/estado |
| `POST /admin/documents` | ADMIN | ingesta multipart |
| `DELETE /admin/documents/{id}` | ADMIN | baja lógica + eliminación vectorial |

Ejemplo:

```http
POST /api/v1/conversations/5/messages
Authorization: Bearer ey...
Content-Type: application/json
X-Correlation-ID: 31f5...

{"content":"Resume CVE-2021-44228 y el procedimiento de contención"}
```

La respuesta contiene `answer`, `sources`, `degradedSources` y `correlationId`; nunca devuelve prompts internos, tokens PII ni trazas.
