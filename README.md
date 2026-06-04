# Sistema de Gestión de Workflows - Fase 1

Proyecto base para el sistema de workflows con Spring Boot, MongoDB, Angular y Tailwind CSS.

## Estructura

- `backend/`: API REST con Spring Boot 3, Maven y MongoDB.
- `frontend/`: Angular standalone con Tailwind CSS y sidebar base.
- `docker-compose.yml`: MongoDB local.

## Requisitos

- Java 17+
- Maven
- Node.js 20+
- Angular CLI
- Docker Desktop opcional
- VS Code

## Levantar MongoDB

```bash
docker compose up -d
```

## Ejecutar backend

```bash
cd backend
mvn spring-boot:run
```

API base:

```text
http://localhost:8080/api
```

Endpoints Fase 1:

```text
GET     /api/users
GET     /api/users/{id}
POST    /api/users
PUT     /api/users/{id}
DELETE  /api/users/{id}
PATCH   /api/users/{id}/activate
PATCH   /api/users/{id}/deactivate

GET     /api/roles
POST    /api/roles
PUT     /api/roles/{id}
DELETE  /api/roles/{id}

GET     /api/departments
POST    /api/departments
PUT     /api/departments/{id}
DELETE  /api/departments/{id}
```

## Ejecutar frontend

```bash
cd frontend
npm install
npm start
```

Frontend:

```text
http://localhost:4200
```

## Probar datos en Postman

Crear rol:

```json
{
  "nombre": "ADMIN",
  "permisos": ["USERS_READ", "USERS_WRITE", "ROLES_READ", "ROLES_WRITE"]
}
```

Crear departamento:

```json
{
  "nombre": "Sistemas",
  "descripcion": "Departamento de sistemas"
}
```

Crear usuario:

```json
{
  "nombre": "Administrador",
  "email": "admin@workflow.com",
  "password": "123456",
  "rol": "ADMIN",
  "departamentoId": null,
  "activo": true
}
```

## Nota

Esta fase todavía no incluye JWT. La seguridad se implementa en la Fase 2.
