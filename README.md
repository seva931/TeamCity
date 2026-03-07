# TeamCity Test Automation

API- и UI-тесты для TeamCity на Java 21.

**Стек:** JUnit 5, RestAssured, Selenide, AssertJ, Lombok, Jackson, Allure, GitHub Actions.

---

## О проекте

Проект покрывает:
- REST API TeamCity
- UI TeamCity через Selenide
- автогенерацию тестовых данных через JUnit 5 extensions

Основная идея:
- данные для тестов создаются до теста аннотациями
- после теста cleanup выполняется автоматически

---

## Документация


- [Запуск окружения через Docker Compose](docs/docker-compose.md)
- [Тесты: режимы запуска и отчёт](docs/test-run.md)
- [Тесты: написание и структура](docs/test-writing.md)

## CI

Основной workflow: `.github/workflows/run-tests.yml`
- запускает тесты (`mvn test -Pselenoid`)
- генерирует Allure-отчёт (`mvn allure:report`)
- загружает артефакты прогона
- публикует отчёт в GitHub Pages для событий `push`

---

## Структура проекта

```text
src/main/java/
├── api/            # модели, реквестеры, спеки
├── common/         # генераторы, enum, общие утилиты
├── jupiter/        # аннотации и JUnit 5 extensions
└── ui/             # page objects и UI-компоненты

src/test/java/
├── api/            # API-тесты
└── ui/             # UI-тесты

infra/
├── docker-compose.yml
├── browsers.json
├── .env.example
└── .env
```
