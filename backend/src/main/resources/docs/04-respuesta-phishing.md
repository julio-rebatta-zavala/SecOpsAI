# Respuesta a phishing

**Código:** SEC-IR-003 · **Versión:** 1.5

Preserve el correo original con cabeceras. Extraiga remitente, Reply-To, Return-Path, Message-ID, URLs y hashes de adjuntos sin abrirlos. Busque mensajes relacionados en el tenant y determine si hubo clic, descarga, autenticación o consentimiento OAuth.

Si hubo exposición de credenciales: revocar sesiones, restablecer contraseña, verificar MFA, revisar reglas de buzón y aplicaciones consentidas. Si hubo ejecución: aplicar el procedimiento de malware. Retire el mensaje de otros buzones solo con herramientas autorizadas. Comunique al usuario en tono no culpabilizante.
