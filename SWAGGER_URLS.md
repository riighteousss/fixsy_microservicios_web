# Swagger, rutas y credenciales - Fixsy Parts

## Swagger UI y OpenAPI (localhost)
- Usuarios (8081): UI http://localhost:8081/swagger-ui  | JSON http://localhost:8081/api-docs  | Base `/api/v1`
- Productos (8083): UI http://localhost:8083/swagger-ui/index.html  | JSON http://localhost:8083/api-docs
- Ordenes (8084): UI http://localhost:8084/swagger-ui  | JSON http://localhost:8084/api-docs
- Mensajes/Tickets (8085): UI http://localhost:8085/swagger-ui  | JSON http://localhost:8085/api-docs
///Puedes usar usuario fixsy y contraseña Fixsy2025! para acceder a http://localhost:8084/swagger-ui y http://localhost:8085/swagger-ui (y sus /api-docs). Reinicia cada servicio después de cambiar las propiedades para que se apliquen.
## Rutas base de cada API
- Usuarios: `/api/v1/users`, `/api/v1/roles`
- Productos: `/api/products`
- Ordenes: `/api/orders`
- Mensajes/Tickets: `/api/tickets`, `/api/messages`

## Credenciales de prueba (login JWT en usuarios)
- Admin (Administrador): `admin@admin.fixsy.com` / `Admin123`
- Soporte: `soporte@soporte.fixsy.com` / `Soporte123`
- Vendedor: `vendedor@vendedor.fixsy.com` / `Vendedor123`
- Cliente: `cliente@cliente.fixsy.com` / `Cliente123`
- Usuario (compat): `usuario@fixsy.com` / `Usuario123`

Flujo: POST a `/api/v1/users/login` con email/password -> usar `token` en header `Authorization: Bearer <token>` para endpoints protegidos.

## Tips rapidos de base de datos (MySQL)
- Host local: `jdbc:mysql://localhost:3306/<db>?createDatabaseIfNotExist=true`
- Usuario por defecto: `root` | Password: vacio (segun application.properties)
- Bases usadas: `fixsy_usuarios`, `fixsy_productos`, `fixsy_ordenes`, `fixsy_mensajes`
- DDL: `spring.jpa.hibernate.ddl-auto=update` (crea/ajusta tablas automaticamente)
- Ver datos rapido:
  ```sql
  SHOW DATABASES;
  USE fixsy_productos;
  SHOW TABLES;
  SELECT * FROM products LIMIT 5;
  ```
- Seed de ejemplo: `productos/src/main/java/com/fixsy/productos/config/ProductosDataLoader.java` crea productos iniciales si la tabla esta vacia.
- Config por servicio:
  - usuarios/src/main/resources/application.properties
  - productos/src/main/resources/application.properties
  - ordenes/src/main/resources/application.properties
  - mensajes/src/main/resources/application.properties

## Levantar microservicios en local
```bash
cd usuarios  && mvn spring-boot:run   # 8081
cd productos && mvn spring-boot:run   # 8083
cd ordenes  && mvn spring-boot:run    # 8084
cd mensajes && mvn spring-boot:run    # 8085
```
