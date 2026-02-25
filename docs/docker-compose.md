# Docker Compose: локальное окружение TeamCity

## Что поднимаем

Через `docker compose` поднимаются сервисы:
- `teamcity-server`
- `teamcity-agent-1`
- `teamcity-agent-2`
- `swagger-ui`
- `selenoid` (опционально, через профиль)
- `selenoid-ui` (опционально, через профиль)

Файлы инфраструктуры лежат в директории `infra/`.

---

## Предварительные требования

- Docker Desktop (с поддержкой Docker Compose)
- Java 21
- Maven 3.9+

---

## Конфигурационные файлы

```text
infra/
├── docker-compose.yml
├── browsers.json
├── .env.example
└── .env
```

### Как работать с `.env`

`.env.example` хранится в git как шаблон.
Локально используйте `.env`:

```bash
cp infra/.env.example infra/.env
```

Текущие переменные:

```properties
TEAMCITY_VERSION=2025.11.2
TEAMCITY_PORT=8111
SWAGGER_UI_PORT=8082
SELENOID_PORT=4444
SELENOID_UI_PORT=8080
TEAMCITY_AGENT_1_NAME=tc-agent-1
TEAMCITY_AGENT_2_NAME=tc-agent-2
```

---

## Первый запуск (локальный режим)

```bash
cd infra
docker compose up -d
docker compose ps
```

Проверить в браузере:
- TeamCity: `http://localhost:8111`
- Swagger UI: `http://localhost:8082`

Если это первый запуск на этой машине, пройти визард TeamCity:
1. выбрать `Internal (HSQLDB)`
2. принять лицензию
3. создать пользователя (для учебного стенда можно `admin/admin`)

---

## Запуск с Selenoid

Скачать браузерный образ (один раз):

```bash
docker pull selenoid/vnc_chrome:128.0
```

Поднять профиль `selenoid`:

```bash
cd infra
docker compose --profile selenoid up -d
docker compose --profile selenoid ps
```

Проверки:
- Selenoid status: `http://localhost:4444/status`
- Selenoid UI: `http://localhost:8080`

Проверка сессии через curl:

```bash
curl -X POST "http://localhost:4444/wd/hub/session" \
  -H "Content-Type: application/json" \
  -d '{
    "capabilities": {
      "alwaysMatch": {
        "browserName": "chrome",
        "browserVersion": "128.0",
        "selenoid:options": {
          "enableVNC": true
        }
      }
    }
  }'
```

---

## Управление окружением

Из директории `infra`:

```bash
docker compose stop
```
Останавливает контейнеры, данные сохраняются.

```bash
docker compose restart
```
Перезапускает контейнеры, данные сохраняются.

```bash
docker compose down
```
Удаляет контейнеры и сеть, volume сохраняются.

```bash
docker compose --profile selenoid down -v
```
Полный сброс (включая volume). После этого TeamCity нужно настраивать заново.
