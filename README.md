## Restaurant Website

A simple full-stack restaurant web app with a static frontend and a Spring Boot backend. Users can browse the menu, register/login (JWT), place orders, make table reservations, and leave reviews. The backend uses MySQL and exposes REST APIs; the frontend is plain HTML/CSS/JS.

### Repository structure
```
Restaurant-website/
├─ Backend-restaurant-website/
│  └─ Restaurant-website/
│     ├─ pom.xml
│     ├─ mvnw, mvnw.cmd
│     └─ src/
│        ├─ main/java/org/example/restaurantwebsite/
│        │  ├─ config/ (security, CORS, JWT helper)
│        │  ├─ controller/ (Auth, Menu, Orders, Reservations, Profile, Reviews, etc.)
│        │  ├─ dto/, model/, repository/, service/, security/
│        │  └─ RestaurantWebsiteApplication.java
│        └─ main/resources/
│           └─ error404.html
├─ Front-restaurant-website/
│  ├─ index.html
│  ├─ css/, js/, authorization/, страницы/, images*/
│  └─ adminSTR/
├─ RestaurantDB.sql (MySQL dump with schema and demo data)
└─ README.md
```

### Tech stack
- Backend: Java 17, Spring Boot 3, Spring Security, Spring Data JPA, JWT, Cloudinary client, Telegram Bots
- Database: MySQL 8
- Frontend: HTML, CSS, vanilla JS

### Prerequisites
- Java 17+
- MySQL 8+ running locally (or accessible remotely)
- A static web server for the frontend (e.g., VS Code Live Server on port 5500)

### Backend setup
1) Create database and import demo data
   - Create a database named `restaurant` in MySQL
   - Import `RestaurantDB.sql` into that database

2) Create application.properties
   Create `Backend-restaurant-website/Restaurant-website/src/main/resources/application.properties` with your settings:
```properties
# Server
server.port=8080

# MySQL datasource
spring.datasource.url=jdbc:mysql://localhost:3306/restaurant?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT (base64-encoded secret for HS512)
jwt.secret=PASTE_BASE64_SECRET_HERE

# Cloudinary (optional, required for image upload endpoints)
cloudinary.cloud_name=YOUR_CLOUD_NAME
cloudinary.api_key=YOUR_API_KEY
cloudinary.api_secret=YOUR_API_SECRET
```

3) Generate a JWT secret
   - Option A: Run the helper class `JwtSecretGenerator` and copy the printed secret
   - Option B: Generate any 64+ byte random key and base64-encode it

4) Run the backend
   - Windows PowerShell:
     ```powershell
     cd Backend-restaurant-website\Restaurant-website
     .\mvnw.cmd spring-boot:run
     ```
   - macOS/Linux:
     ```bash
     cd Backend-restaurant-website/Restaurant-website
     ./mvnw spring-boot:run
     ```

The API will be available at `http://localhost:8080` by default.

### Frontend setup
1) Serve the static files in `Front-restaurant-website/` with a simple web server (recommended: Live Server on port 5500)

2) Configure API base URL
   Update the backend host in these files to point to your running backend (search for `API_URL` or `http://192.168.1.70:8080`):
   - `Front-restaurant-website/authorization/auth.js`
   - `Front-restaurant-website/js/profile.js`
   - `Front-restaurant-website/js/menu.js`
   - `Front-restaurant-website/js/reservation.js`
   - `Front-restaurant-website/js/script.js` (reviews fetch)

   Example:
   ```js
   const API_URL = "http://localhost:8080";
   ```

3) CORS notes
   - CORS is configured in the backend (`config/SecurityConfig.java`, `config/WebConfig.java`, `config/SimpleCorsFilter.java`).
   - If you serve the frontend on a different origin than `http://localhost:5500`, add it to allowed origins.

### Key API endpoints (non-exhaustive)
- Auth: `POST /api/register`, `POST /api/login`, `POST /api/logout`, `GET /api/user-info`
- Menu: `GET /api/menu`, `GET /api/menu/{id}`
- Orders: `POST /api/orders` (Authorization: Bearer <token>)
- Reservations: `POST /api/reserve` (Bearer), `GET /api/profile/reservations` (Bearer), `DELETE /api/profile/reservations/{id}` (Bearer)
- Profile: `GET /api/profile/orders` (Bearer), `GET /api/profile/favorite-dishes` (Bearer)
- Reviews: `GET /api/reviews/latest`, `POST /api/reviews` (Bearer)

JWT is expected in the `Authorization: Bearer <token>` header for protected endpoints.

### Tips
- If you change the backend port, also update the frontend `API_URL` and any hardcoded fetch URLs
- For Cloudinary uploads, set the three Cloudinary properties; otherwise, image upload endpoints will fail

---

## Веб‑приложение ресторана

Полноценное приложение с статическим фронтендом и бэкендом на Spring Boot. Пользователи могут просматривать меню, регистрироваться/входить (JWT), оформлять заказы, бронировать столики и оставлять отзывы. Бэкенд использует MySQL и REST API; фронтенд на HTML/CSS/JS.

### Структура репозитория
```
Restaurant-website/
├─ Backend-restaurant-website/… (Spring Boot, Java, контроллеры, сервисы и т.д.)
├─ Front-restaurant-website/… (index.html, css/, js/, authorization/, страницы/)
└─ RestaurantDB.sql (дамп MySQL со схемой и демо-данными)
```

### Технологии
- Бэкенд: Java 17, Spring Boot 3, Security, JPA, JWT, Cloudinary, Telegram Bots
- База данных: MySQL 8
- Фронтенд: HTML, CSS, чистый JS

### Требования
- Java 17+
- MySQL 8+
- Простой сервер для фронтенда (например, Live Server на 5500 порту)

### Настройка бэкенда
1) Создайте БД и импортируйте демо‑данные
   - База `restaurant`
   - Импортируйте `RestaurantDB.sql`

2) Создайте `application.properties`
   Добавьте файл `Backend-restaurant-website/Restaurant-website/src/main/resources/application.properties`:
```properties
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/restaurant?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=ВАШ_ПОЛЬЗОВАТЕЛЬ
spring.datasource.password=ВАШ_ПАРОЛЬ
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

jwt.secret=ВСТАВЬТЕ_BASE64_СЕКРЕТ

cloudinary.cloud_name=ИМЯ_ОБЛАКА
cloudinary.api_key=API_KEY
cloudinary.api_secret=API_SECRET
```

3) Сгенерируйте JWT‑секрет
   - Вариант A: выполните `JwtSecretGenerator` и возьмите выведенный ключ
   - Вариант B: сгенерируйте случайный ключ 64+ байта и закодируйте в base64

4) Запустите бэкенд
   - Windows PowerShell:
     ```powershell
     cd Backend-restaurant-website\Restaurant-website
     .\mvnw.cmd spring-boot:run
     ```
   - macOS/Linux:
     ```bash
     cd Backend-restaurant-website/Restaurant-website
     ./mvnw spring-boot:run
     ```

API по умолчанию доступен на `http://localhost:8080`.

### Настройка фронтенда
1) Запустите статический сервер в `Front-restaurant-website/` (рекомендуется Live Server на 5500)

2) Укажите адрес бэкенда
   Обновите host в файлах (ищите `API_URL` или `http://192.168.1.70:8080`):
   - `Front-restaurant-website/authorization/auth.js`
   - `Front-restaurant-website/js/profile.js`
   - `Front-restaurant-website/js/menu.js`
   - `Front-restaurant-website/js/reservation.js`
   - `Front-restaurant-website/js/script.js`

   Пример:
   ```js
   const API_URL = "http://localhost:8080";
   ```

3) Примечание по CORS
   - CORS настраивается на бэкенде (см. `config/WebConfig.java`, `config/SimpleCorsFilter.java`, `config/SecurityConfig.java`)
   - Если фронтенд запущен не на `http://localhost:5500`, добавьте его origin в whitelist

### Основные эндпоинты (неполный список)
- Авторизация: `POST /api/register`, `POST /api/login`, `POST /api/logout`, `GET /api/user-info`
- Меню: `GET /api/menu`, `GET /api/menu/{id}`
- Заказы: `POST /api/orders` (требуется Bearer‑токен)
- Бронирования: `POST /api/reserve` (Bearer), `GET /api/profile/reservations` (Bearer), `DELETE /api/profile/reservations/{id}` (Bearer)
- Профиль: `GET /api/profile/orders` (Bearer), `GET /api/profile/favorite-dishes` (Bearer)
- Отзывы: `GET /api/reviews/latest`, `POST /api/reviews` (Bearer)

Для защищённых эндпоинтов передавайте JWT в заголовке `Authorization: Bearer <token>`.

### Полезно знать
- При смене порта бэкенда обновите `API_URL` на фронтенде и все жёстко прописанные URL
- Для загрузки изображений настройте переменные Cloudinary, иначе соответствующие эндпоинты будут возвращать ошибки


