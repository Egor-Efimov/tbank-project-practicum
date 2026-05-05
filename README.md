# Умный дом — платформа для управления умным домом

## Технологии

- Java 21, Spring Boot 3.4.4
- PostgreSQL 16
- Kafka + Zookeeper
- Docker + Docker Compose
- Flyway (миграции БД)
- Prometheus + Grafana (мониторинг)

---

## Запуск

docker-compose down

docker-compose up -d

## Работоспособность

## 1. API

curl.exe -X POST "http://localhost:8080/api/rooms/kitchen"

curl.exe -X GET "http://localhost:8080/api/rooms"

## 2. Actuator

curl.exe -s "http://localhost:8080/actuator/health"

## 3. Миграции БД

docker exec smarthome-db psql -U postgres -d smarthome -c "SELECT version, success FROM flyway_schema_history ORDER BY
installed_rank;"