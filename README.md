# Dopamine Shop — Backend

## Sprint 1
- JWT-аутентификация (`/auth/register`, `/auth/login`, `/auth/refresh`)
- Каталог товаров с keyset-пагинацией (`GET /products`, `GET /products/{id}`, `POST /products`)
- Flyway-миграции схемы (`users`, `products`)

## Sprint 2 — Корзина
- `GET /cart` — текущая корзина пользователя (требует Bearer-токен)
- `POST /cart/items` — добавить товар (`{"productId": "...", "quantity": 1}`). Если товар уже в корзине — количество суммируется, а не перезаписывается.
- `PATCH /cart/items/{productId}` — установить точное количество (`{"quantity": 3}`)
- `DELETE /cart/items/{productId}` — убрать товар из корзины
- Ответ всегда содержит пересчитанные `totalAmount` и `totalItemsCount`, чтобы фронту не нужно было считать суммы самому.

## Запуск локально (Windows)

Нужны Java 21 и запущенный Docker Desktop. Maven устанавливать не требуется: в проекте есть Maven Wrapper.

1. Поднять Postgres:
   ```
   docker compose up -d
   ```

2. Запустить API в локальном профиле (из корня проекта):
   ```powershell
   .\mvnw.cmd spring-boot:run '-Dspring-boot.run.profiles=dev'
   ```

3. По умолчанию приложение поднимется на `http://localhost:8080`.

Каталог не загружается из сети при обычном запуске. Чтобы один раз импортировать товары из DummyJSON, запустите API с профилями `dev,seed`:

```powershell
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.profiles=dev,seed'
```

Повторный импорт пропускает товары с уже известным `external_id`. Профиль `seed` требует доступа к DummyJSON.

## Переменные окружения

| Переменная | Назначение | Дефолт |
|---|---|---|
| `DB_URL` | JDBC URL PostgreSQL | `jdbc:postgresql://localhost:5432/dopamine_shop` только в профиле `dev` |
| `DB_USERNAME` | пользователь Postgres | `postgres` только в профиле `dev` |
| `DB_PASSWORD` | пароль Postgres | `postgres` только в профиле `dev` |
| `JWT_SECRET` | секрет для подписи JWT (мин. 32 символа) | локальное значение только в профиле `dev` |

Без профиля `dev` переменные `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` и `JWT_SECRET` обязательны. Flyway применяет миграции при запуске; автоматическая очистка БД отключена.

## Проверки

```powershell
.\mvnw.cmd test
```

Для интеграционного теста оформления заказа нужен запущенный Docker Engine: тест поднимает временный PostgreSQL через Testcontainers. Без Docker он пропускается; остальные тесты запускаются. Backend CI использует Java 21. Flutter-клиент проверяется отдельным CI в собственном репозитории.

## Проверка API (curl)

```bash
# Регистрация
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password123","displayName":"Test"}'

# Создание товара (нужен accessToken из ответа выше)
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <accessToken>" \
  -d '{"title":"Designer Bag","brand":"Acme","price":350000,"category":"bags","imageUrl":"https://example.com/bag.jpg","exclusive":true}'

# Лента товаров (публичная)
curl http://localhost:8080/products

# Добавить товар в корзину
curl -X POST http://localhost:8080/cart/items \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <accessToken>" \
  -d '{"productId":"<id товара>","quantity":1}'

# Посмотреть корзину
curl http://localhost:8080/cart -H "Authorization: Bearer <accessToken>"
```

## Заказы и профиль

- Модуль `order`: оформление заказа (`checkout`), `OrderStatus`-машина, `OrderStatusScheduler`
- `POST /orders/checkout` создает заказ из текущей корзины, очищает корзину и обновляет статистику пользователя.
- `GET /orders`, `GET /orders/{id}` возвращают историю заказов и таймлайн статусов.
- `POST /devices/register` сохраняет FCM-токен устройства для будущих push-уведомлений.
- `GET /users/me/stats` возвращает totalSavedAmount, totalOrdersCount и любимые категории.

## Flutter app

Flutter-клиент — отдельный Git-репозиторий: `https://github.com/Lupastic/dopamine_shop_app_front`. Для совместной локальной работы его можно клонировать в `dopamine_shop_app` рядом с backend-файлами. Эта папка игнорируется корневым Git.

```bash
cd dopamine_shop_app
flutter pub get
flutter run --dart-define API_BASE_URL=http://10.0.2.2:8080
```

Для desktop/web можно использовать `http://localhost:8080`, для Android emulator обычно нужен `http://10.0.2.2:8080`.
