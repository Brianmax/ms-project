# ms-project

Repositorio multi-módulo de Spring Boot con dos servicios de negocio (`product-service` y `order-service`) y tres módulos de infraestructura de Spring Cloud (`eureka-server`, `config-server`, `api-gateway`).

## Módulos

| Módulo            | Puerto | Descripción                                                                       | Estado            |
|-------------------|--------|-----------------------------------------------------------------------------------|-------------------|
| `product-service` | 8081   | API REST del catálogo de productos. Base de datos Postgres propia.                | Implementado      |
| `order-service`   | 8082   | API REST de pedidos. Llama a `product-service` vía OpenFeign con fallback local.  | Implementado      |
| `eureka-server`   | 8761   | Descubrimiento de servicios.                                                      | Solo andamiaje    |
| `config-server`   | 8888   | Configuración centralizada (Spring Cloud Config, perfil `native`).                | Solo andamiaje    |
| `api-gateway`     | 8080   | Gateway de borde (Spring Cloud Gateway).                                          | Solo andamiaje    |

Los servicios de negocio comparten:

- base de datos Postgres independiente por servicio
- APIs REST basadas en DTOs con validación de peticiones
- manejo global de excepciones
- logs de aplicación estructurados

Los módulos de infraestructura compilan y arrancan, pero los servicios de negocio aún no se registran en Eureka, no leen del Config Server y no pasan por el gateway.

## Estructura del proyecto

```text
product-service
└── src/main/java/com/ms1/product_service
    ├── controller
    ├── service
    ├── repository
    ├── entity
    ├── dto
    ├── mapper
    ├── exception
    └── logging

order-service
└── src/main/java/com/ms2/order_service
    ├── controller
    ├── service
    ├── repository
    ├── entity
    ├── dto
    ├── mapper
    ├── client          # cliente OpenFeign de product-service y gateway de fallback
    ├── exception
    └── logging
```

Ambos servicios siguen una arquitectura n-layer.

## Ejecutar localmente

Requisitos previos:

- Java 21
- Maven Wrapper incluido en cada módulo (`./mvnw`)
- Postgres disponible en `localhost:5432` (base `product_db`) y `localhost:5433` (base `order_db`). La forma más simple es levantarlos con Docker Compose; ver [Ejecutar con Docker Compose](#ejecutar-con-docker-compose).

Levanta primero `product-service`:

```bash
cd product-service
./mvnw spring-boot:run
```

En otra terminal, levanta `order-service`:

```bash
cd order-service
./mvnw spring-boot:run
```

Puertos y bases de datos por defecto:

- `product-service`: `8081`, Postgres en `localhost:5432`, base `product_db`
- `order-service`: `8082`, Postgres en `localhost:5433`, base `order_db`

`order-service` llama a `product-service` con la URL fija:

```properties
clients.product-service.url=http://localhost:8081
```

## Ejemplos de API

Crear un producto:

```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mechanical Keyboard",
    "sku": "KEY-001",
    "price": 89.90
  }'
```

Respuesta de ejemplo:

```json
{
  "id": 1,
  "name": "Mechanical Keyboard",
  "sku": "KEY-001",
  "price": 89.90,
  "createdAt": "2026-04-24T10:15:30.123"
}
```

Crear un pedido:

```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

Respuesta de ejemplo:

```json
{
  "id": 1,
  "quantity": 2,
  "totalPrice": 179.80,
  "status": "CREATED",
  "createdAt": "2026-04-24T10:18:44.321",
  "product": {
    "id": 1,
    "name": "Mechanical Keyboard",
    "sku": "KEY-001",
    "unitPrice": 89.90,
    "source": "LIVE"
  }
}
```

Leer un pedido:

```bash
curl http://localhost:8082/api/orders/1
```

Si `product-service` no está disponible al leer un pedido, `order-service` devuelve la copia local del producto y marca el origen como `FALLBACK`.

## Ejecutar con Docker Compose

El archivo `docker-compose.yml` define dos perfiles para que arranques solo lo que necesites:

- `services`: Postgres más los dos servicios de negocio
- `all`: lo anterior más `eureka-server`, `config-server` y `api-gateway`

Levantar solo los servicios de negocio:

```bash
docker compose --profile services up --build
```

Levantar el stack completo, incluida la infraestructura:

```bash
docker compose --profile all up --build
```

Aviso: `docker compose up` sin perfil no arranca nada, porque todos los servicios están detrás de un perfil.

Contenedores y puertos en host:

- `postgres-product` en `5432`
- `postgres-order` en `5433`
- `product-service` en `8081`
- `order-service` en `8082`
- `eureka-server` en `8761` (perfil `all`)
- `config-server` en `8888` (perfil `all`)
- `api-gateway` en `8080` (perfil `all`)
- un volumen Postgres persistente por base de datos

Dentro de la red de Docker, `order-service` llama a `product-service` por nombre de servicio:

```text
http://product-service:8081
```

## Próximos pasos

- Registrar `product-service` y `order-service` en `eureka-server` y reemplazar la URL fija de OpenFeign por descubrimiento por nombre de servicio.
- Externalizar la configuración de los servicios de negocio en `config-server` (perfil `native`, carpeta `configs/`) y conectarlos como clientes de Spring Cloud Config.
- Exponer el tráfico público a través de `api-gateway`, definiendo rutas hacia `/api/products/**` y `/api/orders/**`.
- Añadir pruebas automatizadas (unitarias y de integración) en ambos servicios de negocio.
- Definir una estrategia de migraciones de base de datos (por ejemplo Flyway o Liquibase) en lugar de `spring.jpa.hibernate.ddl-auto=update`.
