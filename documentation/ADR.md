# Registro resumido de decisiones

1. **Orquestación determinista, no tool-calling libre.** Reduce prompt injection y hace auditable cada salida.
2. **Spring AI para modelos y embeddings; LangChain4j para parsing/chunking.** Evita dos orquestadores competidores y satisface responsabilidades claras.
3. **Qdrant por REST local.** Aísla el adaptador y mantiene control de payload/filtros.
4. **PostgreSQL en vez de H2.** Soporta cifrado de aplicación, índices, locking y producción local reproducible.
5. **Presidio como servicio local.** Mantiene PII dentro de la red y permite modelos/recognizers propios para español.
6. **Conversación cifrada y auditoría sanitizada.** Conserva evidencia completa sin convertir logs en una fuga.
7. **JWT propio solo para la referencia.** En producción se prefiere OIDC/OAuth2 con un IdP y MFA.
8. **Documentos envenenados siempre fuera de `/docs`.** Evita ingesta automática accidental; se usan solo en pruebas manuales/controladas.
