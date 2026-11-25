# Gestión de vulnerabilidades

**Código:** SEC-VM-001 · **Versión:** 3.1

La prioridad no depende solo de CVSS. Considere explotación conocida, exposición a Internet, criticidad del activo, privilegios, controles compensatorios y facilidad de remediación. Valide que CPE, versión y configuración realmente sean afectados.

Proceso: descubrir → normalizar → correlacionar con inventario → validar → priorizar → remediar o mitigar → verificar → cerrar. Registre fuente, fecha y evidencia. Para CVE use NVD como una fuente, no como única verdad; contraste el aviso del fabricante y CISA KEV cuando aplique.

SLA de referencia: crítico explotado 24 h, crítico 7 días, alto 30, medio 90, bajo según ciclo. Una excepción requiere propietario, justificación, controles compensatorios, vencimiento y aceptación formal del riesgo.
