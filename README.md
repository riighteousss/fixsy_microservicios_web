# Fixsy Parts - Arquitectura de Microservicios

Este proyecto implementa una arquitectura de microservicios para la plataforma "Fixsy Parts" utilizando Spring Boot.

## Microservicios

El proyecto está compuesto por los siguientes microservicios:

- **usuarios**: Gestiona todo lo relacionado con usuarios, roles, autenticación y autorización.
- **productos**: Gestiona el catálogo de productos y repuestos.
- **ordenes**: Gestiona las órdenes de compra de los clientes.
- **mensajes**: Gestiona los tickets de soporte y la mensajería interna.

## Microservicio de Usuarios (`usuarios`)

Este es el servicio central para la seguridad de la aplicación.

### Funcionalidades Implementadas

1.  **Autenticación y Autorización con Spring Security y JWT**:
    -   **Registro de Usuarios**: Endpoint público para que nuevos usuarios se registren. Por defecto, se les asigna el rol `CLIENTE`.
    -   **Inicio de Sesión (Login)**: Endpoint público que, con credenciales válidas (username/password), devuelve un token JWT.
    -   **Validación de Token**: Un filtro intercepta cada petición a endpoints protegidos, valida el token JWT presente en la cabecera `Authorization: Bearer <token>` y establece el contexto de seguridad.

2.  **Estructura del Código**:
    -   `model`: Contiene las entidades JPA (`User`) y enumeraciones (`Role`).
    -   `repository`: Interfaces de Spring Data JPA para interactuar con la base de datos.
    -   `dto`: Objetos de Transferencia de Datos para las peticiones y respuestas de la API (ej. `LoginRequest`, `AuthResponse`).
    -   `service`: Contiene la lógica de negocio. `AuthService` para el registro/login y `JwtService` para la creación y validación de tokens.
    -   `config`: Clases de configuración de Spring Security (`SecurityConfig`, `ApplicationConfig`) y el filtro de autenticación JWT (`JwtAuthenticationFilter`).
    -   `controller`: Controladores REST que exponen los endpoints de la API.

3.  **Roles**:
    -   `ADMIN`: Acceso total.
    -   `VENDEDOR`: Acceso a productos y órdenes.
    -   `CLIENTE`: Acceso a la tienda (registro por defecto).

### Documentación de la API (Swagger)

Una vez que el microservicio de `usuarios` esté en ejecución, puedes acceder a la documentación interactiva de la API a través de Swagger UI.

**Link de Swagger para el servicio de Usuarios:**

- **Usuarios (8081):** http://localhost:8081/swagger-ui/index.html
- **Productos (8083):** http://localhost:8083/swagger-ui/index.html
- **Órdenes (8084):** http://localhost:8084/swagger-ui/index.html
- **Mensajes (8085):** http://localhost:8085/swagger-ui/index.html

Desde esta interfaz podrás ver todos los endpoints disponibles, sus parámetros, y probarlos directamente.

**Nota:** Para probar los endpoints protegidos, primero debes obtener un token JWT desde el endpoint `/auth/login` del servicio de usuarios y luego añadirlo en la cabecera `Authorization` como `Bearer <token>` en tus peticiones a los otros servicios.