# OnlineOrder

A food ordering web app: browse restaurants and menus, sign up / log in, and manage a shopping cart.

## Tech stack

- **Frontend** (`frontend/`): React 18, Ant Design 4, Create React App
- **Backend** (`backend/`): Java 21, Spring Boot 4, Spring Security, Spring Data JDBC, Caffeine cache
- **Database**: PostgreSQL 15 (via Docker Compose)

## Project structure

```
.
├── frontend/   # React single-page app
└── backend/    # Spring Boot REST API (also serves the built frontend from src/main/resources/public)
```

## Getting started

### 1. Start the database

```bash
cd backend
docker compose up -d
```

### 2. Run the backend

```bash
cd backend
./gradlew bootRun
```

The API runs on http://localhost:8080. Database connection settings can be overridden with the
`DATABASE_URL`, `DATABASE_PORT`, `DATABASE_USERNAME` and `DATABASE_PASSWORD` environment variables.

### 3. Run the frontend

```bash
cd frontend
npm install
npm start
```

The dev server runs on http://localhost:3000 and proxies API requests to the backend.

## Building for deployment

```bash
cd frontend && npm run build
cp -r build/* ../backend/src/main/resources/public/
cd ../backend && ./gradlew bootJar
docker build -t onlineorder .
```
