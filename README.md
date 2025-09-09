# Warehouse Query API

Purpose
- Read-side service (CQRS) that provides denormalized product availability.
- Computes buildableQuantity per product by combining product definitions with current article stock.

Architecture at a glance
- Input: MongoDB documents (products, articles) materialized by the consumer.
- Processing: for each product, buildableQuantity = min(floor(article.stock / requiredAmount)) across required articles; 0 if any are missing.
- Output: paginated, read-optimized responses for UI/services.
- Ops: Actuator health and OpenAPI docs.

Key features
- Paginated product listing with computed buildableQuantity.
- Read-only projections; no mutations.
- Structured logging and health metrics.

Tech stack
- Java 21, Spring Boot 3.x
- Spring Web
- Spring Data MongoDB
- Actuator, springdoc-openapi
- Docker/Docker Compose
- OpenTelemetry (optional), Lombok (if present), Maven

API
- GET /api/v1/products?page={page}&size={size}
  - Returns items like: { id, name, buildableQuantity }
- GET /actuator/health
- Swagger UI: /swagger-ui.html

Configuration
- Port: PORT (default 8083)
- MongoDB: SPRING_DATA_MONGODB_URI (or MONGODB_URI) and database
- Profiles: mongo, logging, management

Run locally
- Prereqs: Java 21, Maven, MongoDB; ensure warehouse-data-consumer has populated data
- Maven: mvn spring-boot:run -Dspring-boot.run.profiles=logging,management,mongo
- JAR: mvn package && java --enable-preview -jar target/app.jar
- Docker: docker build -t warehouse-query-api .; run with Compose

How to test
- Preload via ingestion or command flows so the consumer persists documents.
- Fetch products:
  - curl "http://localhost:8083/api/v1/products?page=0&size=10"
- Sanity check example:
  - If the product requires [4×A, 1×B] and stocks are A=12, B=5, expect buildableQuantity = min(floor(12/4), floor(5/1)) = 3.