# Fixsy Parts - Microservicios Backend

Backend de microservicios para **Fixsy Parts**, una tienda online de repuestos automotrices.

## 🏗️ Arquitectura

El sistema está compuesto por 4 microservicios independientes:

| Microservicio | Puerto | Base de Datos | Descripción |
|---------------|--------|---------------|-------------|
| **usuarios** | 8081 | fixsy_usuarios | Gestión de usuarios, autenticación y roles |
| **productos** | 8083 | fixsy_productos | Catálogo de productos/repuestos |
| **ordenes** | 8084 | fixsy_ordenes | Órdenes de compra y seguimiento |
| **mensajes** | 8085 | fixsy_mensajes | Tickets de soporte y mensajería |

## 📋 Requisitos

- Java 21+
- Maven 3.8+
- MySQL 8.0+
- (Opcional) Docker y Docker Compose

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd microserviciosfixsy-main
```

### 2. Configurar Base de Datos

Asegúrate de tener MySQL corriendo. Las bases de datos se crean automáticamente.

Edita los archivos `application.properties` de cada microservicio si necesitas cambiar credenciales:

```properties
spring.datasource.username=root
spring.datasource.password=tu_contraseña
```

### 3. Ejecutar cada microservicio

```bash
# Terminal 1 - Usuarios
cd usuarios
mvn spring-boot:run

# Terminal 2 - Productos
cd productos
mvn spring-boot:run

# Terminal 3 - Órdenes
cd ordenes
mvn spring-boot:run

# Terminal 4 - Mensajes/Tickets
cd mensajes
mvn spring-boot:run
```

## 📖 Documentación API (Swagger)

Una vez iniciados los microservicios, accede a la documentación interactiva:

| Microservicio | Swagger UI |
|---------------|------------|
| Usuarios | http://localhost:8081/swagger-ui |
| Productos | http://localhost:8083/swagger-ui |
| Órdenes | http://localhost:8084/swagger-ui |
| Mensajes | http://localhost:8085/swagger-ui |

## 🔌 Endpoints Principales

### Usuarios (Puerto 8081)

```
# Usuarios
POST   /api/users/register      # Registrar usuario (rol por dominio de email)
POST   /api/users/login         # Iniciar sesión
GET    /api/users               # Listar usuarios
GET    /api/users/{id}          # Obtener usuario por ID
GET    /api/users/email/{email} # Obtener usuario por email
GET    /api/users/role/{role}   # Obtener usuarios por rol
GET    /api/users/status/{status} # Obtener usuarios por estado
PUT    /api/users/{id}          # Actualizar usuario
PUT    /api/users/{id}/status   # Cambiar estado (Activo/Bloqueado/Suspendido)
PUT    /api/users/{id}/role     # Cambiar rol (Solo Admin)
DELETE /api/users/{id}          # Eliminar usuario

# Roles
GET    /api/roles               # Listar todos los roles
GET    /api/roles/{id}          # Obtener rol por ID
GET    /api/roles/nombre/{nombre} # Obtener rol por nombre
```

**Tabla de Roles (se crea automáticamente):**

| ID | Nombre | Dominio Email | Descripción |
|----|--------|---------------|-------------|
| 1 | Usuario | *(cualquiera)* | Cliente normal de la tienda |
| 2 | Admin | @admin.fixsy.com | Administrador con acceso completo |
| 3 | Soporte | @soporte.fixsy.com | Personal de soporte al cliente |

**Asignación automática de rol por dominio:**
- `usuario@gmail.com` → Rol **Usuario**
- `juan@admin.fixsy.com` → Rol **Admin**
- `maria@soporte.fixsy.com` → Rol **Soporte**

**Estados de usuario:** `Activo`, `Bloqueado`, `Suspendido`

### Productos (Puerto 8083)

```
GET    /api/products            # Listar productos activos
GET    /api/products/all        # Listar todos (incluye inactivos)
GET    /api/products/{id}       # Obtener producto por ID
GET    /api/products/sku/{sku}  # Obtener producto por SKU
GET    /api/products/featured   # Productos destacados
GET    /api/products/on-sale    # Productos en oferta
GET    /api/products/category/{cat}  # Por categoría
GET    /api/products/marca/{marca}   # Por marca
GET    /api/products/search?q=texto  # Buscar por nombre
GET    /api/products/categorias      # Listar categorías
GET    /api/products/marcas          # Listar marcas
POST   /api/products            # Crear producto
PUT    /api/products/{id}       # Actualizar producto
PUT    /api/products/{id}/stock # Actualizar stock
PUT    /api/products/{id}/featured  # Toggle destacado
PUT    /api/products/{id}/active    # Toggle activo
DELETE /api/products/{id}       # Eliminar producto
```

### Órdenes (Puerto 8084)

```
GET    /api/orders              # Listar todas las órdenes
GET    /api/orders/{id}         # Obtener orden por ID
GET    /api/orders/user/{userId}    # Órdenes por usuario
GET    /api/orders/email/{email}    # Órdenes por email
GET    /api/orders/status/{status}  # Órdenes por estado
GET    /api/orders/pending-shipment # Órdenes pendientes de envío
GET    /api/orders/tracking/{num}   # Buscar por tracking
POST   /api/orders              # Crear orden
PUT    /api/orders/{id}/status  # Actualizar estado
PUT    /api/orders/{id}/tracking    # Agregar número de seguimiento
PUT    /api/orders/{id}/payment     # Registrar pago
DELETE /api/orders/{id}         # Eliminar orden

# Estadísticas
GET    /api/orders/stats/total-sales      # Total ventas
GET    /api/orders/stats/count/{status}   # Contar por estado
```

**Estados de orden:** `Pendiente`, `Pagado`, `Enviado`, `Entregado`, `Cancelado`

### Mensajes/Tickets (Puerto 8085)

```
# Tickets
GET    /api/tickets                  # Listar todos los tickets
GET    /api/tickets/{id}             # Ticket por ID (con mensajes)
GET    /api/tickets/user/{userId}    # Tickets de un usuario
GET    /api/tickets/email/{email}    # Tickets por email
GET    /api/tickets/estado/{estado}  # Tickets por estado
GET    /api/tickets/categoria/{cat}  # Tickets por categoría
GET    /api/tickets/assigned/{supportId} # Tickets asignados a soporte
GET    /api/tickets/unassigned       # Tickets sin asignar
GET    /api/tickets/pending          # Tickets pendientes (por prioridad)
GET    /api/tickets/order/{orderId}  # Tickets de una orden
POST   /api/tickets                  # Crear ticket (con mensaje inicial)
PUT    /api/tickets/{id}/estado      # Cambiar estado
PUT    /api/tickets/{id}/prioridad   # Cambiar prioridad
PUT    /api/tickets/{id}/assign      # Asignar a soporte
DELETE /api/tickets/{id}             # Eliminar ticket

# Mensajes
GET    /api/messages/ticket/{ticketId}   # Mensajes de un ticket
POST   /api/messages                     # Enviar mensaje
PUT    /api/messages/ticket/{id}/read    # Marcar como leídos
GET    /api/messages/unread/user/{userId}    # No leídos (usuario)
GET    /api/messages/unread/support/{id}     # No leídos (soporte)
```

**Estados de ticket:** `Abierto`, `En Proceso`, `Resuelto`, `Cerrado`

**Categorías:** `Consulta`, `Reclamo`, `Devolución`, `Problema Técnico`, `Otro`

**Prioridades:** `Baja`, `Media`, `Alta`, `Urgente`

## 📝 Ejemplos de Uso

### Registrar Usuario (Cliente normal)
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "cliente@example.com",
    "password": "password123",
    "nombre": "Juan",
    "apellido": "Pérez",
    "phone": "+56912345678"
  }'
# → Se asigna automáticamente rol "Usuario"
```

### Registrar Admin (por dominio de email)
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@admin.fixsy.com",
    "password": "password123",
    "nombre": "María",
    "apellido": "García",
    "phone": "+56987654321"
  }'
# → Se asigna automáticamente rol "Admin" por el dominio @admin.fixsy.com
```

### Registrar Soporte (por dominio de email)
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "soporte1@soporte.fixsy.com",
    "password": "password123",
    "nombre": "Carlos",
    "apellido": "López",
    "phone": "+56911223344"
  }'
# → Se asigna automáticamente rol "Soporte" por el dominio @soporte.fixsy.com
```

### Login
```bash
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "cliente@example.com",
    "password": "password123"
  }'
```

### Crear Producto
```bash
curl -X POST http://localhost:8083/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Filtro de aceite",
    "descripcion": "Filtro de aceite estándar para motores 1.6-2.0L",
    "precio": 9990,
    "stock": 50,
    "tags": ["motor", "mantenimiento"],
    "categoria": "Filtros",
    "marca": "Bosch"
  }'
```

### Crear Orden
```bash
curl -X POST http://localhost:8084/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "userEmail": "cliente@example.com",
    "userName": "Juan Pérez",
    "items": [
      {
        "productId": 1,
        "productName": "Filtro de aceite",
        "quantity": 2,
        "unitPrice": 9990
      }
    ],
    "shippingAddress": "Av. Principal 123",
    "shippingRegion": "Metropolitana",
    "shippingComuna": "Santiago",
    "contactPhone": "+56912345678"
  }'
```

### Crear Ticket de Soporte
```bash
curl -X POST http://localhost:8085/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "userEmail": "cliente@example.com",
    "userName": "Juan Pérez",
    "asunto": "Problema con mi pedido",
    "categoria": "Reclamo",
    "prioridad": "Alta",
    "orderId": 123,
    "mensajeInicial": "Hola, mi pedido llegó incompleto..."
  }'
```

### Enviar Mensaje en Ticket
```bash
curl -X POST http://localhost:8085/api/messages \
  -H "Content-Type: application/json" \
  -d '{
    "ticketId": 1,
    "senderId": 5,
    "senderEmail": "soporte1@soporte.fixsy.com",
    "senderName": "María García",
    "senderRole": "Soporte",
    "contenido": "Hola Juan, lamentamos el inconveniente. Ya revisamos tu pedido..."
  }'
```

## 🔧 Configuración para Frontend

### CORS

Los microservicios ya tienen CORS habilitado con `@CrossOrigin(origins = "*")` para desarrollo.

**Para producción**, configura dominios específicos en cada controlador:

```java
@CrossOrigin(origins = {"https://tudominio.com", "https://www.tudominio.com"})
```

### Conexión desde React (Fixsy Parts)

Ejemplo de servicio API para el frontend:

```typescript
// src/services/api.ts
const API_BASE = {
  users: 'http://localhost:8081/api/users',
  products: 'http://localhost:8083/api/products',
  orders: 'http://localhost:8084/api/orders'
};

export const api = {
  // Usuarios
  login: (email: string, password: string) =>
    fetch(`${API_BASE.users}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    }).then(r => r.json()),

  register: (userData: any) =>
    fetch(`${API_BASE.users}/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData)
    }).then(r => r.json()),

  // Productos
  getProducts: () =>
    fetch(`${API_BASE.products}`).then(r => r.json()),

  getProductById: (id: number) =>
    fetch(`${API_BASE.products}/${id}`).then(r => r.json()),

  searchProducts: (query: string) =>
    fetch(`${API_BASE.products}/search?q=${encodeURIComponent(query)}`)
      .then(r => r.json()),

  // Órdenes
  createOrder: (orderData: any) =>
    fetch(`${API_BASE.orders}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(orderData)
    }).then(r => r.json()),

  getOrdersByUser: (userId: number) =>
    fetch(`${API_BASE.orders}/user/${userId}`).then(r => r.json())
};
```

## 🗂️ Estructura del Proyecto

```
microserviciosfixsy-main/
├── usuarios/                    # Microservicio de Usuarios
│   ├── src/main/java/com/fixsy/usuarios/
│   │   ├── controller/
│   │   │   ├── UserController.java
│   │   │   └── RoleController.java
│   │   ├── dto/
│   │   │   ├── UserDTO.java
│   │   │   ├── RoleDTO.java
│   │   │   └── ...
│   │   ├── model/
│   │   │   ├── User.java        # Entidad usuario
│   │   │   └── Role.java        # Entidad rol (tabla separada)
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── RoleRepository.java
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   └── RoleService.java  # Inicializa roles automáticamente
│   │   └── config/
│   └── pom.xml
│
├── productos/                   # Microservicio de Productos
│   ├── src/main/java/com/fixsy/productos/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   └── config/
│   └── pom.xml
│
├── ordenes/                     # Microservicio de Órdenes
│   ├── src/main/java/com/fixsy/ordenes/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   └── config/
│   └── pom.xml
│
├── mensajes/                    # Microservicio de Mensajes/Tickets
│   ├── src/main/java/com/fixsy/mensajes/
│   │   ├── controller/
│   │   │   ├── TicketController.java
│   │   │   └── MessageController.java
│   │   ├── dto/
│   │   ├── model/
│   │   │   ├── Ticket.java
│   │   │   └── Message.java
│   │   ├── repository/
│   │   ├── service/
│   │   └── config/
│   └── pom.xml
│
└── README.md
```

## 🔐 Seguridad (Recomendaciones para Producción)

1. **Hashear contraseñas** con BCrypt
2. **Implementar JWT** para autenticación
3. **Configurar CORS** con dominios específicos
4. **Usar HTTPS** en todos los endpoints
5. **Validar roles** en endpoints sensibles

## 📊 Monitoreo

Cada microservicio expone métricas en:
- Health check: `/actuator/health`
- API Docs: `/api-docs`

## 🤝 Contribuir

1. Fork el proyecto
2. Crea tu rama de feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT.
