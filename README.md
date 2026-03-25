# BeeAtlas Notifications Management

Сервис управления подписками и отправкой уведомлений в рамках платформы BeeAtlas.

## Что делает сервис

- хранит подписки пользователей на сущности;
- принимает бизнес-события и формирует уведомления;
- взаимодействует с RabbitMQ для обработки событий;
- использует PostgreSQL для хранения данных;
- применяет Flyway для миграций схемы БД.

## Технологии

- Java 17
- Spring Boot 2.7.x
- Spring Web, Spring Data JPA, Spring AMQP
- PostgreSQL
- RabbitMQ
- Flyway
- Actuator + Prometheus metrics

## Быстрый старт (Docker Compose)

В репозитории есть `docker-compose.yml`, который поднимает:

- `notifications-service` (приложение);
- `notifications-service-postgres` (PostgreSQL);
- `rabbitmq` (RabbitMQ + management UI).

### 1) Запуск

```bash
docker compose up -d --build
```

### 2) Проверка состояния

```bash
docker compose ps
curl http://localhost:8081/actuator/health
```

По умолчанию приложение доступно на `http://localhost:8081`.

## Локальный запуск без Docker

### Требования

- JDK 17
- Maven 3.8+
- PostgreSQL 15+
- RabbitMQ 3.x

### Шаги

1. Поднимите PostgreSQL и RabbitMQ.
2. Настройте переменные окружения (см. ниже).
3. Запустите приложение:

```bash
mvn clean spring-boot:run
```

## Переменные окружения

Ниже ключевые переменные, которые используются при запуске в Docker Compose:

### PostgreSQL

- `NOTIFICATIONS_POSTGRES_HOST` (по умолчанию: `notifications-service-postgres`)
- `NOTIFICATIONS_POSTGRES_DB` (по умолчанию: `notifications_service`)
- `NOTIFICATIONS_POSTGRES_USER` (по умолчанию: `postgres`)
- `NOTIFICATIONS_POSTGRES_PASSWORD` (по умолчанию: `postgres`)
- `NOTIFICATIONS_SERVICE_POSTGRES_NODEPORT` (по умолчанию: `5433`)

### RabbitMQ

- `RABBITMQ_HOST` (по умолчанию: `rabbitmq`)
- `RABBITMQ_PORT` (по умолчанию: `5672`)
- `RABBITMQ_USER` (по умолчанию: `guest`)
- `RABBITMQ_PASSWORD` (по умолчанию: `guest`)
- `RABBITMQ_VHOST` (по умолчанию: `/`)
- `RABBITMQ_EXCHANGE` (по умолчанию: `capability.exchange`)
- `RABBITMQ_ROUTING_KEY` (по умолчанию: `capability.routing`)

### Внешние интеграции

- `INTEGRATION_CAPABILITY_SERVER_URL`
- `INTEGRATION_FRONTEND_SERVER_URL`
- `INTEGRATION_AUTH_SERVER_URL`
- `INTEGRATION_AUTHSSO_SERVER_URL`

### Наблюдаемость

- `OTEL_EXPORTER_OTLP_ENDPOINT`

## Основные HTTP эндпоинты

Базовые маршруты сервиса:

- `GET /` - базовый endpoint доступности приложения;
- `GET /api/v1/subscribe/{entityType}` - подписки по типу сущности;
- `GET /api/v1/subscribe` - подписки текущего пользователя;
- `POST /api/v1/subscribe/{entityType}/{id}` - подписаться на сущность;
- `DELETE /api/v1/subscribe/{entityType}/{id}` - отписаться от сущности;
- `GET /api/v1/business/notify` - получение бизнес-уведомлений;
- `GET /api/v1/notify/change-type` - список типов изменений;
- `GET /api/v1/notify/entity-type` - список типов сущностей;
- `POST /api/v1/notify/business-event/{entity_type}/{entity_id}` - отправка бизнес-события;
- `POST /api/v1/notify/business-event/group/role/{role}/{entity_type}/{entity_id}` - отправка события для группы/роли.

## Actuator и метрики

- Health: `GET /actuator/health`
- Info: `GET /actuator/info`
- Metrics: `GET /actuator/metrics`
- Prometheus: `GET /actuator/prometheus`

## Swagger

Для API-документации используется Springfox. Обычно UI доступен по адресу:

- `http://localhost:8081/swagger-ui/`
- или `http://localhost:8081/swagger-ui/index.html`

## Сборка и тесты

```bash
mvn clean package
mvn test
```

## Структура очередей RabbitMQ

При старте Docker Compose автоматически создаются очереди:

- `tech_queue`
- `notification`
- `change_tech_capability`
- `change_business_capability`
- `tech_capability`

## Лицензия

Проект распространяется в соответствии с файлом `LICENSE`.