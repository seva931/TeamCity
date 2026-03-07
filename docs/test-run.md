# Тесты: режимы запуска и отчёт

## Режимы запуска

Переключение между локальным браузером и Selenoid делается через Maven profiles в `pom.xml`:
- `local` (по умолчанию)
- `selenoid`

### Команды запуска

```bash
mvn test                                   # local (по умолчанию)
mvn test -Pselenoid                        # через Selenoid

mvn test -Dtest.groups=web                 # только @Tag("web"), local
mvn test -Dtest.groups=api                 # только @Tag("api"), local
mvn test -Pselenoid -Dtest.groups=web      # только @Tag("web"), Selenoid
```

---

## Результаты JUnit (Surefire)

После `mvn test` Maven сохраняет результаты выполнения тестов в:
- `target/surefire-reports/`

Это XML/текстовые файлы для диагностики падений и интеграций CI.

---

## Отчёт Allure

Сгенерировать статический отчёт:

```bash
mvn allure:report
```

Открыть отчёт локально:

```bash
mvn allure:serve
```

Если `allure-results` пустой, сначала запустите тесты:

```bash
mvn test
mvn allure:report
```

При падении UI-тестов в Allure автоматически прикладывается скриншот с именем `Screenshot on fail`.

---

## Allure в CI (GitHub Actions)

Workflow `.github/workflows/run-tests.yml` после тестов:
- генерирует отчёт `mvn allure:report`
- загружает артефакты `allure-results` и `target/site/allure-maven-plugin`
- публикует отчёт в GitHub Pages (для `push`, не для `pull_request`)
