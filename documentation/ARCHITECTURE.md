# Arquitectura detallada

## Objetivos y atributos de calidad

SecOps responde consultas operativas con evidencia de procedimientos internos y NVD, evita que datos personales salgan sin protección, deja una traza auditable y aplica mínimo privilegio. Prioridades: seguridad > privacidad > corrección > disponibilidad > latencia.

## Contenedores

| Componente | Responsabilidad | Confianza |
|---|---|---|
| Angular 22 | UX, rutas y visibilidad por rol; nunca autoridad | No confiable |
| API Spring Boot | autenticación, autorización, orquestación y políticas | Confiable |
| PostgreSQL | identidad, prompts versionados, conversaciones cifradas, metadatos y auditoría | Restringido |
| Qdrant | chunks y metadatos no sensibles aprobados | Restringido |
| Presidio | análisis local de PII | Restringido |
| NVD | datos públicos de vulnerabilidades | Externo |
| OpenRouter | inferencia de chat/embeddings tokenizados | Externo |

## Flujo de consulta (fail closed)

1. `ChatController` autentica JWT y valida tamaño/formato.
2. `PolicyGuardrailService` normaliza NFKC y busca atributos protegidos y patrones de prompt injection. Un hallazgo produce 422, auditoría y retorno inmediato.
3. Se guarda el mensaje original **cifrado con AES-256-GCM** para auditoría autorizada.
4. `PresidioService` analiza localmente; `PiiTokenizationService` reemplaza spans desde el final por tokens únicos. El mapa token→valor vive solo durante la petición.
5. `SecOpsOrchestrator` usa reglas deterministas: CVE explícito activa NVD; cualquier consulta válida activa RAG. El LLM nunca decide qué herramienta privilegiada ejecutar.
6. RAG embebe el texto tokenizado mediante OpenRouter y consulta Qdrant. El contexto recuperado se vuelve a inspeccionar; instrucciones o secretos se descartan.
7. NVD recibe solo el identificador CVE validado, no la pregunta completa.
8. El modelo recibe prompt no secreto, consulta tokenizada y contexto delimitado como datos no confiables.
9. `OutputGuardrailService` bloquea fugas del canario, secretos, prompt o instrucciones internas.
10. Se restauran los tokens PII localmente, se cifra y persiste la respuesta, y se emiten eventos de auditoría sanitizados.

## Orquestador

No es un “agente libre”. Es una máquina de estados explícita: `RECEIVED → POLICY_APPROVED → PII_TOKENIZED → SOURCES_COLLECTED → GENERATED → OUTPUT_APPROVED → COMPLETED`, con estados terminales `POLICY_BLOCKED`, `QUARANTINED` y `FAILED`. Esta elección reduce inyección, bucles y uso imprevisible de herramientas.

## RAG e ingesta

- Inicio: archivos Markdown/TXT/PDF de `backend/src/main/resources/docs`.
- Manual: multipart solo ADMIN, 5 MiB, nombre generado, MIME y extensión en lista permitida.
- SHA-256 del binario + restricción única evita duplicados exactos.
- `DocumentSecurityScanner` busca instrucciones de modelo, exfiltración, URLs de webhook, secretos y contenido binario inesperado. Sospechosos quedan `QUARANTINED`.
- LangChain4j/Tika extrae y divide; Spring AI crea embeddings; Qdrant guarda puntos con `documentId`, `chunk`, `source`, `classification=INTERNAL`.
- Se recomienda agregar ClamAV/CDR para PDF en producción. Los archivos originales no se publican desde un web root.

## Persistencia

Tablas principales: `app_users`, `conversations`, `messages`, `audit_events`, `rag_documents`, `system_prompts`, `refresh_tokens`. Flyway crea restricciones e índices. `messages.content_ciphertext` contiene IV+ciphertext autenticado; auditoría usa hashes, categorías y conteos, nunca el mensaje.

## Resiliencia

| Dependencia | Circuit breaker | Retry | Rate limiter | Bulkhead | Timeout |
|---|---:|---:|---:|---:|---:|
| OpenRouter chat | Sí | solo 429/5xx | Sí | Sí | Sí |
| OpenRouter embeddings | Sí | Sí | Sí | Sí | Sí |
| NVD | Sí | Sí con backoff | Sí | Sí | Sí |
| Presidio | Sí | corto | local | Sí | Sí |
| Qdrant | Sí | lectura; upsert idempotente | No | Sí | Sí |

No se reintentan 4xx, autenticación, bloqueos de política ni operaciones no idempotentes. Las degradaciones son explícitas: si NVD falla, puede responder con RAG marcando la fuente no disponible; si Presidio falla, la consulta se bloquea (fail closed).

## Escalado

API sin sesión de servidor; JWT corto y refresh rotatorio. PostgreSQL coordina estado. Para varias réplicas, mover rate limiting a gateway/Redis y usar outbox para SIEM. Qdrant puede pasar a clúster sin cambiar el contrato interno.
