# Backup y recuperación ante ransomware

**Código:** SEC-BCP-009 · **Versión:** 2.1

Use estrategia 3-2-1 con una copia offline o inmutable. Separe credenciales de backup del dominio de producción, exija MFA y pruebe restauraciones. Mida RPO/RTO por servicio.

Ante ransomware, no conecte backups al entorno comprometido. Determine la primera fecha confiable, erradique persistencia y restaure en red limpia. Valide hash, configuración, parches, identidad y monitoreo antes de volver a producción. Conserve evidencia y coordine decisiones legales y de comunicación.
