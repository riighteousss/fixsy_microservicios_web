# 📖 URLs de Documentación Swagger - Fixsy Parts

## Microservicios Disponibles (4)

### 1. Usuarios (Puerto 8081)
- **Swagger UI:** http://localhost:8081/swagger-ui
- **OpenAPI JSON:** http://localhost:8081/api-docs

### 2. Productos (Puerto 8083)
- **Swagger UI:** http://localhost:8083/swagger-ui
- **OpenAPI JSON:** http://localhost:8083/api-docs

### 3. Órdenes (Puerto 8084)
- **Swagger UI:** http://localhost:8084/swagger-ui
- **OpenAPI JSON:** http://localhost:8084/api-docs

### 4. Mensajes/Tickets (Puerto 8085)
- **Swagger UI:** http://localhost:8085/swagger-ui
- **OpenAPI JSON:** http://localhost:8085/api-docs

## Resumen de Endpoints

| Microservicio | Base URL | Funcionalidad |
|---------------|----------|---------------|
| Usuarios | `/api/users`, `/api/roles` | Registro, login, gestión de usuarios y roles |
| Productos | `/api/products` | Catálogo de repuestos |
| Órdenes | `/api/orders` | Compras y seguimiento |
| Mensajes | `/api/tickets`, `/api/messages` | Soporte al cliente |

## Para iniciar los microservicios

```bash
# Cada uno en su terminal
cd usuarios && mvn spring-boot:run    # Puerto 8081
cd productos && mvn spring-boot:run   # Puerto 8083
cd ordenes && mvn spring-boot:run     # Puerto 8084
cd mensajes && mvn spring-boot:run    # Puerto 8085
```

## Bases de Datos (MySQL)

| Microservicio | Base de Datos |
|---------------|---------------|
| usuarios | fixsy_usuarios |
| productos | fixsy_productos |
| ordenes | fixsy_ordenes |
| mensajes | fixsy_mensajes |

*Las bases de datos se crean automáticamente al iniciar cada microservicio.*
