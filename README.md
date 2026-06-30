# YAML Parser for GitLab CI

YAML parser для `.gitlab-ci.yml` с упрощенной структурой, написанный in Kotlin.

Поддержка обязательного списка `stages` и произвольного количества jobs с обязательным полем `image` (может быть строкой
или объектом с полем `name`).

## Features

- Парсинг YAML в Kotlin-объекты через SnakeYAML
- Поддержка двух форматов `image`: строка или объект с `name` (остальные поля игнорируются)
- Структурированное логирование (Logback + kotlin-logging)
- Тесты с покрытием позитивных и негативных сценариев

## Запуск

```bash
# С указанием конкретного файла
./gradlew run --args="samples/valid.yml"
```

```bash
# С файлом по умолчанию (.gitlab-ci.yml)
./gradlew run
```

## Tests

```bash
./gradlew test
```