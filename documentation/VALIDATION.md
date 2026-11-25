# Estado de validación

Fecha: 22-11-2026.

## Ejecutado en el entorno de generación

- Validación de sintaxis Java con `javac -encoding UTF-8 -proc:none`: sin diagnósticos sintácticos; los diagnósticos restantes fueron exclusivamente clases externas no disponibles en el classpath local.
- Validación sintáctica TypeScript con `tsc --noResolve`: sin errores de parser; los diagnósticos restantes corresponden a módulos Angular/PrimeNG no instalados.
- Parseo correcto de todos los archivos JSON y YAML.
- `scripts/verify-project.sh`: correcto.
- Conteos comprobados: 12 documentos automáticos, 3 documentos manuales y 2 documentos envenenados.
- Estructura comprobada: 64 fuentes Java principales, 5 pruebas Java y 28 fuentes TypeScript.

## Limitación del entorno

No fue posible ejecutar la compilación Maven ni instalar dependencias npm porque el proxy de paquetes del entorno devolvió HTTP 403. Esto no se oculta como una compilación exitosa. El repositorio incluye Maven Wrapper, versiones fijadas, pruebas y workflow de CI para ejecutar la verificación completa en una red con acceso a Maven Central y npm.

## Verificación requerida al descargar

```bash
cd backend && ./mvnw -B verify
cd ../frontend && npm install && npm run build && npm test -- --watch=false
cd .. && ./scripts/verify-project.sh
```

Antes de producción, ejecute además SCA/SBOM, escaneo de contenedores, DAST, pruebas de carga, pruebas de recuperación y los casos de `TEST-CASES.md` contra servicios reales o stubs controlados.
