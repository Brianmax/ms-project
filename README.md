# Teaching Project - Phase 1 APIs

This repository contains two Spring Boot APIs:

- `product-service`: owns the product catalog
- `order-service`: owns orders and reads product data from `product-service` through OpenFeign

Each service has:

- its own database (`H2` file database for this phase)
- DTO-based REST APIs
- global exception handling
- request validation
- structured application logs

## Project structure

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
    ├── application
    ├── domain
    ├── infrastructure
    └── interfaces
```

Architecture note:

- `product-service` uses n-layer architecture
- `order-service` still uses the earlier hexagonal-style structure

## Run locally

Start `product-service` first:

```bash
cd product-service
./mvnw spring-boot:run
```

Start `order-service` in another terminal:

```bash
cd order-service
./mvnw spring-boot:run
```

The default ports are:

- `product-service`: `8081`
- `order-service`: `8082`

`order-service` calls `product-service` using the fixed base URL:

```properties
clients.product-service.url=http://localhost:8081
```

## Example API flow

Create a product:

```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mechanical Keyboard",
    "sku": "KEY-001",
    "price": 89.90
  }'
```

Example response:

```json
{
  "id": 1,
  "name": "Mechanical Keyboard",
  "sku": "KEY-001",
  "price": 89.90,
  "createdAt": "2026-04-24T10:15:30.123"
}
```

Create an order:

```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

Example response:

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

Read an order:

```bash
curl http://localhost:8082/api/orders/1
```

If `product-service` is temporarily unavailable during an order read, `order-service` returns the product snapshot stored locally and marks the source as `FALLBACK`.

## Run with Docker Compose

Build and start both services:

```bash
docker compose up --build
```

The compose setup includes:

- `product-service` on port `8081`
- `order-service` on port `8082`
- one persistent H2 volume per service

Inside Docker, `order-service` calls `product-service` with the service name:

```text
http://product-service:8081
```
