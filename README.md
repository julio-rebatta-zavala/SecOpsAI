# SecOps AI Assistant

Proyecto educativo y de referencia para un asistente de ciberseguridad con **RAG local en Qdrant**, consulta a **NVD API 2.0**, modelos de chat y embeddings mediante **OpenRouter**, guardrails deterministas, anonimización basada en **Microsoft Presidio**, auditoría, JWT/RBAC y una interfaz **Angular 22 + PrimeNG 22**.

> Fecha de la línea base: 22-11-2026. Versiones fijadas: Spring Boot 4.1.2, Spring AI 2.0.2, LangChain4j 1.24.0, Resilience4j 2.4.0, Angular 22 y PrimeNG 22.1.5. Antes de producción, valide compatibilidad y aplique actualizaciones de seguridad.

## 1. Arquitectura resumida

```text
Browser (Angular/PrimeNG)
        | HTTPS + JWT
        v
Spring Boot API ── PostgreSQL (usuarios, conversaciones cifradas, auditoría)
  |  preflight guardrails (sesgo/inyección) -- corta antes de cualquier salida
  |  Presidio local (detección PII) -> tokens efímeros
  |  Orquestador determinista
  +---- Qdrant local (RAG)
  +---- NVD API 2.0 (CVE)
  +---- OpenRouter (chat + embeddings; solo texto tokenizado)
```

El orden de controles es una propiedad de seguridad: **normalización → filtro de atributos protegidos → anti-inyección → detección/tokenización PII → recuperación → saneamiento de contexto → modelo → validación de salida → restauración local de PII**. Si el filtro de sesgo o inyección falla, no se llama a Presidio, Qdrant, NVD ni OpenRouter.

## 2. Requisitos

- Docker Engine 27+ y Docker Compose v2.
- 8 GB RAM libres (recomendado: 12 GB).
- Una clave de OpenRouter y, opcionalmente, una clave de NVD.
- Para desarrollo sin contenedores: JDK 17+ (recomendado JDK 25 LTS), Maven 3.9+, Node 24+ y npm 11+.

## 3. Descargar y levantar exactamente

```bash
git clone <URL-DE-SU-REPOSITORIO> secops-ai
cd secops-ai
cp .env.example .env
```

Edite `.env` y asigne como mínimo:

```dotenv
OPENROUTER_API_KEY=sk-or-v1-...
JWT_SECRET=<base64-de-64-bytes-aleatorios>
DATA_ENCRYPTION_KEY=<base64-de-32-bytes-aleatorios>
```

Genere las dos claves así:

```bash
openssl rand -base64 64 | tr -d '\n'; echo
openssl rand -base64 32 | tr -d '\n'; echo
```

Arranque todo:

```bash
docker compose up --build -d
docker compose ps
```

Espere a que `backend`, `postgres`, `qdrant`, `presidio-analyzer` y `presidio-anonymizer` estén saludables. Abra:

- Aplicación: http://localhost:4200
- API/OpenAPI: http://localhost:8080/swagger-ui.html
- Actuator: http://localhost:8080/actuator/health
- Qdrant: http://localhost:6333/dashboard

Usuarios de demostración (solo si `SEED_DEMO_USERS=true`):

| Rol | Usuario | Contraseña inicial |
|---|---|---|
| ADMIN | `admin` | `ChangeMe_Admin_2026!` |
| AUDIT | `auditor` | `ChangeMe_Audit_2026!` |
| USER | `user` | `ChangeMe_User_2026!` |

Todos quedan con `mustChangePassword=true`. No active usuarios semilla en producción.

## 4. Verificación rápida

```bash
# API sana
curl -fsS http://localhost:8080/actuator/health

# Pruebas backend
cd backend && ./mvnw test && cd ..

# Pruebas/build frontend
cd frontend && npm install && npm test -- --watch=false && npm run build && cd ..

# Validaciones estáticas del repositorio
./scripts/verify-project.sh
```

Flujo manual:

1. Inicie sesión como `user`, cambie la contraseña y abra **Conversaciones**.
2. Pregunte `¿Qué debo revisar para CVE-2021-44228?`.
3. Pruebe PII: `Mi correo es ana@example.com; ayúdame con CVE-2021-44228`. En auditoría debe constar detección/tokenización; OpenRouter solo recibe `<PII_EMAIL_ADDRESS_1>`.
4. Pruebe bloqueo: `Prioriza a los usuarios por nacionalidad`. Debe obtener HTTP 422 `POLICY_PROTECTED_ATTRIBUTE`, sin eventos `RAG_QUERY`, `NVD_CALL` ni `MODEL_CALL` con el mismo `correlationId`.
5. Como `admin`, cargue archivos de `test-data/manual-ingestion/`; repita uno para observar `DOCUMENT_DUPLICATE`.
6. Cargue archivos de `test-data/poisoned/`; deben quedar `QUARANTINED` y nunca indexarse.
7. Como `auditor`, revise conversaciones y auditoría; no puede chatear ni administrar.

## 5. Configuración de modelos

Valores predeterminados, configurables en `.env`:

```dotenv
OPENROUTER_CHAT_MODEL=openai/gpt-4.1-mini
OPENROUTER_EMBEDDING_MODEL=openai/text-embedding-3-small
OPENROUTER_EMBEDDING_DIMENSIONS=1536
```

No cambie el modelo de embeddings sobre una colección existente. Cree otra colección o reindexe todos los documentos para evitar vectores incompatibles.

## 6. Roles y permisos

| Función | USER | AUDIT | ADMIN |
|---|:---:|:---:|:---:|
| Consultar al agente | Sí | No | Sí |
| Ver conversaciones propias | Sí | No | Sí |
| Ver todas las conversaciones | No | Sí | Sí |
| Ver auditoría/logs sanitizados | No | Sí | Sí |
| Cargar/eliminar/reindexar RAG | No | No | Sí |
| Usuarios y system prompt | No | No | Sí |
| Cambiar contraseña propia | Sí | Sí | Sí |

La comprobación existe en rutas HTTP, `@PreAuthorize` y filtros de propiedad; ocultar botones en Angular no se considera control de seguridad.

## 7. Documentación

- [Arquitectura detallada](documentation/ARCHITECTURE.md)
- [Modelo de amenazas y controles](documentation/SECURITY.md)
- [Contratos REST](documentation/API.md)
- [Casos de prueba](documentation/TEST-CASES.md)
- [Operación y auditoría](documentation/OPERATIONS.md)
- [Decisiones técnicas](documentation/ADR.md)
- [Estado de validación](documentation/VALIDATION.md)

## 8. Límites deliberados

Es una referencia educativa, no una certificación ni un producto listo para datos regulados. En producción use TLS extremo a extremo, IdP/OIDC corporativo, Vault/KMS/HSM, WAF/API gateway, SIEM inmutable, escaneo antimalware real, copias cifradas, SBOM, firma de imágenes, SAST/DAST/SCA y revisión humana de las reglas de sesgo. La restauración de PII se hace solo con un mapa efímero en memoria; los logs nunca contienen el texto original.
