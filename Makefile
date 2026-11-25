.PHONY: up down logs test verify clean
up:
	docker compose up --build -d
down:
	docker compose down
logs:
	docker compose logs -f --tail=200
test:
	cd backend && ./mvnw test
	cd frontend && npm ci && npm test -- --watch=false
verify:
	./scripts/verify-project.sh
clean:
	docker compose down -v --remove-orphans
