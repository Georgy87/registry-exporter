# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Язык общения

**Всегда отвечай на русском языке** — весь текст, объяснения, комментарии к коду и сообщения пользователю.

## Сборка и запуск

```bash
# Сборка модуля offline
./gradlew :offline:build

# Запуск Spring Boot приложения (порт 8082)
./gradlew :offline:bootRun

# Запуск всех тестов
./gradlew :offline:test

# Запуск одного тестового класса
./gradlew :offline:test --tests "org.example.SomeTestClass"

# Применение Liquibase-миграций
./gradlew :offline:update
```

## Структура проекта

Многомодульный Gradle-проект (Kotlin DSL). Активен только модуль `offline`; модуль `online` упомянут в `settings.gradle.kts`, но ещё не создан.

### Модуль `offline` — Spring Boot 3.2, Java 21, смешанный Java/Kotlin

**Доменная область:** межбанковские переводы с формированием бухгалтерских проводок (операционные реестры).

- **`data/entity/`** — JPA-сущности: `Transfer` (межбанковский платёж), `Entry` (проводка по переводу), `Bank`, `TransferFlow`, встраиваемый тип `Amount`
- **`data/repository/`** — Spring Data JPA репозитории; `TransferRepository.kt` + `SchedulerTransferDao` (Kotlin-обёртка) соседствуют с Java-репозиториями
- **`dto/enums/`** — перечисления статусов: `State` (жизненный цикл перевода), `EntryState`, `PaymentFlowType`, `BankName` и др.
- **`service/`** — бизнес-логика:
  - `AbstractEntryService` — стратегия; каждая реализация регистрируется через метод `getSuitablePaymentFlow()`
  - `EntryServiceIdsConfiguration` — собирает `Map<PaymentFlowType, AbstractEntryService>` из всех реализаций `AbstractEntryService`; новые потоки добавляются сюда
  - `OperationEntryService` — маршрутизирует создание проводок к нужной стратегии через карту
  - `OperationReportService` — оркестрирует почасовую генерацию реестров: загрузка подтверждённых переводов → инвалидация устаревших проводок → создание новых
  - `PerformanceOrientedTransferService` (Kotlin) — запрашивает переводы, готовые к отчётности
- **`scheduler/ReportScheduler`** — `@Scheduled(fixedRate=50000)` запускает `OperationReportService`; вызов сейчас закомментирован
- **`configuration/`** — `EntryServiceIdsConfiguration` (карта стратегий), `JpaAuditingConfiguration`

### База данных и Liquibase-миграции

PostgreSQL на `localhost:5432/MyProjectDB` (пользователь: `postgres`).

#### Структура миграций

```
offline/src/main/resources/db/changelog/
  changelog-master.yaml          ← прод: только schema + справочные data
  v1.0/
    schema/
      01-create-bank.yaml
      02-create-transfer.yaml
    data/
      01-insert-banks.sql        ← только обязательные справочники
    testdata/
      01-insert-test-transfers.sql  ← только для тестов, в прод не идут
  v1.1/
    schema/
      01-alter-bank-add-inn.yaml
    data/
      01-update-banks-inn.sql    ← данные, нужные для этого конкретного ALTER

offline/src/test/resources/db/changelog/
  changelog-test.yaml            ← include changelog-master + testdata
```

#### Правила

- `changelog-master.yaml` — только `include`-директивы, никакой логики; **никогда не редактируется задним числом**, только дописывается
- Каждый файл — одна логическая единица (одна таблица или одно изменение)
- Нумерация `01-`, `02-`, ... внутри папки определяет порядок применения
- `data/` — только обязательные справочные данные (банки, валюты, роли)
- `testdata/` — тестовые данные; подключаются только через `changelog-test.yaml`
- Новая версия (`v1.1/`, `v1.2/`) — для ALTER и изменений существующей схемы

#### Как добавить новую миграцию

1. Создать файл: `db/changelog/vX.Y/schema/NN-описание.yaml`
2. Добавить `include` в `changelog-master.yaml`
3. Применить: `./gradlew :offline:update`

#### Тестовое окружение

В `application.yml` для тестов указывать:
```yaml
spring:
  liquibase:
    change-log: classpath:db/changelog/changelog-test.yaml
```

### Добавление нового платёжного потока

1. Создать класс-наследник `AbstractEntryService`, аннотировать `@Service`
2. Реализовать `getSuitablePaymentFlow()`, вернув новое значение `PaymentFlowType`
3. `EntryServiceIdsConfiguration` автоматически зарегистрирует его в карте стратегий

### Смешанный Java/Kotlin

Файлы свободно соседствуют. В Java-классах используется Lombok; в Kotlin-файлах предпочтительно `kotlin-logging-jvm` (`KotlinLogging.logger {}`).