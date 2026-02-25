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
mvn test -Pselenoid -Dtest.groups=api      # только @Tag("api"), Selenoid
```

---

## Отчёт Surefire

Сгенерировать HTML-отчёт:

```bash
mvn surefire-report:report-only
```

Отчёт лежит в файле:
- `target/reports/surefire.html`

Если отчёт пустой или не создаётся, сначала выполните тесты:

```bash
mvn test
mvn surefire-report:report-only
```
