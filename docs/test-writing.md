# Тесты: написание и структура

## Быстрый старт: как написать тест

### API-тест за 5 минут

```java
@ApiTest
@WithBuild
public class MyTest extends BaseTest {

    @Test
    void myTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project,
            @Build CreateBuildTypeResponse build
    ) {
        ProjectResponse response = new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.requestReturnsOk()
        ).get(project.getId());

        softly.assertThat(response.getId()).isEqualTo(project.getId());
    }
}
```

### UI-тест за 5 минут

```java
@WebTest
@WithBuild
public class MyUITest extends BaseUITest {

    @Test
    void myTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        new ProjectsPage().open().shouldContainProjectId(project.getId());
    }
}
```

---

## Аннотации

Аннотации автоматически создают тестовые данные до теста и удаляют после.

### Мета-аннотации (на класс)

| Аннотация | Что делает |
|-----------|-----------|
| `@ApiTest` | Подключает `UserExtension` + тег `api` |
| `@WebTest` | Подключает `UserExtension`, `UiAuthExtension` + тег `web` |
| `@WithBuild` | Подключает `ProjectExtension` и `BuildExtension` |
| `@WithProject` | Подключает только `ProjectExtension` |

### Аннотации параметров (на параметры теста)

| Аннотация | Тип параметра | Что создаёт | Что удаляет |
|-----------|---------------|-------------|-------------|
| `@User` | `CreateUserResponse` | Пользователя | Пользователя |
| `@Project` | `ProjectResponse` | Проект | Проект |
| `@Build` | `CreateBuildTypeResponse` | Билд-конфигурацию | Билд-конфигурацию |

### Примеры параметров аннотаций

```java
@User(role = RoleId.PROJECT_VIEWER) CreateUserResponse user
@Project(projectName = "MyProject") ProjectResponse project
@Build(buildName = "MyBuild") CreateBuildTypeResponse build
```

### `@WithAgent` (на метод)

```java
@Test
@WithAgent(count = 1)
void testWithAgent(Agent[] agents) { ... }
```

Проверяет доступность агентов, блокирует их на время теста и восстанавливает после.

---

## Важно: порядок параметров

`@Build` зависит от `@Project`.

```java
// правильно
void test(@User ... user, @Project ... project, @Build ... build)

// неправильно
void test(@User ... user, @Build ... build)
```

Если `@Build` нужен без `@Project`, задайте `projectId` вручную:

```java
void test(@Build(projectId = "MyProject") CreateBuildTypeResponse build)
```

---

## Как устроены запросы

### 2 базовых класса

| Класс | Возвращает | Когда использовать |
|------|------------|-------------------|
| `CrudRequester` | `ValidatableResponse` | Когда нужен сырой HTTP-ответ |
| `ValidatedCrudRequester<T>` | `T` | Когда нужен сразу DTO-объект |

Оба принимают:
- request spec (кто делает запрос)
- endpoint (куда)
- response spec (какой статус ожидаем)

### Конвенция: степы vs inline-запросы

- Степы (`AdminSteps`, `BuildManageSteps`) используем для `arrange` и `assert`.
- Inline `new CrudRequester(...)` используем для `act`.

---

## `RequestSpecs` и `ResponseSpecs`

### `RequestSpecs`

| Метод | Описание |
|------|----------|
| `RequestSpecs.authAsUser(user)` | Запрос от имени тестового пользователя |
| `RequestSpecs.authAsUser(user, ContentType.TEXT)` | То же, но с другим content type |
| `RequestSpecs.adminSpec()` | Запрос от имени admin из конфига |

### `ResponseSpecs`

| Метод | Статус |
|------|--------|
| `ok()` / `requestReturnsOk()` | 200 |
| `noContent()` | 204 |
| `badRequest()` | 400 |
| `notFound()` | 404 |
| `forbidden()` | 403 |
| `unauthorized()` | 401 |
| `deletesQuietly()` | 200 / 204 / 404 |

---

## `Endpoint`

Примеры:

```java
Endpoint.PROJECTS
Endpoint.PROJECT_ID
Endpoint.BUILD_TYPES
Endpoint.BUILD_TYPES_ID
Endpoint.USERS
Endpoint.AGENTS
Endpoint.BUILD_QUEUE
```

`Endpoint` с параметрами (`%s`) автоматически подставляет id при вызове `.get(id)`, `.delete(id)` и т.д.

---

## Чеклист: новый API-тест

1. Поставить `@ApiTest` (или `@ApiTest` + `@WithBuild` / `@WithProject`).
2. Наследоваться от `BaseTest`.
3. Добавить нужные параметры `@User`, `@Project`, `@Build`.
4. Соблюдать порядок `@User -> @Project -> @Build`.
5. Arrange/assert через степы, act через inline requester.
6. Использовать `softly.assertThat(...)`.

## Чеклист: новый UI-тест

1. Поставить `@WebTest` (и `@WithBuild` / `@WithProject` при необходимости).
2. Наследоваться от `BaseUITest`.
3. Добавить `@User` (логин в браузере произойдёт автоматически).
4. Использовать Page Object'ы из `ui.pages`.
