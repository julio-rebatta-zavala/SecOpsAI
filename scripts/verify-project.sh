#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$root"
base=$(find backend/src/main/resources/docs -maxdepth 1 -type f -name '*.md' | wc -l | tr -d ' ')
manual=$(find test-data/manual-ingestion -maxdepth 1 -type f -name '*.md' | wc -l | tr -d ' ')
poison=$(find test-data/poisoned -maxdepth 1 -type f -name '*.md' | wc -l | tr -d ' ')
[[ "$base" == 12 ]] || { echo "ERROR: docs base=$base, esperado=12"; exit 1; }
[[ "$manual" == 3 ]] || { echo "ERROR: docs manual=$manual, esperado=3"; exit 1; }
[[ "$poison" == 2 ]] || { echo "ERROR: docs poison=$poison, esperado=2"; exit 1; }
for layer in entity dto mapper repository service controller; do
  test -d "backend/src/main/java/com/secops/$layer" || { echo "Falta capa $layer"; exit 1; }
done
test -f backend/src/main/java/com/secops/exception/GlobalExceptionHandler.java
test -f README.md
test -f documentation/ARCHITECTURE.md
if grep -RIE --exclude='.env.example' --exclude='verify-project.sh' --exclude='DocumentSecurityScanner.java' --exclude='OutputGuardrailService.java' --exclude='poison-*.md' '(sk-or-v1-[A-Za-z0-9]{10,}|BEGIN (RSA |EC )?PRIVATE KEY)' .; then
  echo 'ERROR: posible secreto en el repositorio'; exit 1
fi
echo "OK: 12 base, 3 manual, 2 poisoned; estructura y secretos validados"
