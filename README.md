# Envers Filter Demo

A demonstration project showcasing how to use Hibernate Envers for entity auditing in a Spring Boot application, with a focus on filtering and querying entity revisions.

## Project Overview

This project demonstrates how to implement and use Hibernate Envers to track and query the revision history of entities in a Spring Boot application. It provides a simple REST API for creating, updating, and retrieving products, along with their complete revision history.

Key features:
- Entity auditing with Hibernate Envers
- Complex entity relationships with nested objects
- RESTful API for CRUD operations
- Revision history retrieval
- PostgreSQL database integration
- Liquibase for database schema management

## Technologies Used

- **Backend**: Kotlin 1.9.25, Spring Boot 3.5.0
- **Database**: PostgreSQL 16
- **ORM**: Hibernate with Envers for auditing
- **Database Migration**: Liquibase
- **Testing**: JUnit 5, TestContainers
- **Build Tool**: Gradle
- **Infrastructure**: Docker, Docker Compose

## Getting Started

### Prerequisites

- Java 21 or higher
- Docker and Docker Compose

### Setting Up the Development Environment

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/envers-filter-demo.git
   cd envers-filter-demo
   ```

2. **Start the PostgreSQL Database**:
   ```bash
   cd iac
   docker-compose up -d
   ```
   This will start a PostgreSQL 16 container with the following configuration:
   - Database: pocdb
   - Username: user
   - Password: password
   - Port: 5432

3. **Build the Project**:
   ```bash
   ./gradlew build
   ```

4. **Run the Application**:
   ```bash
   ./gradlew bootRun
   ```

The application will be available at http://localhost:8080.

## API Documentation

### Create a Product

**Endpoint**: `POST /api/products`

**Request Body**:
```json
{
  "name": "Sample Product",
  "status": "ACTIVE",
  "technicalDetailsJson": {
    "power": "100kW",
    "torque": "200Nm",
    "additionalProperties": {
      "weight": "1500kg",
      "dimensions": "4.5m x 1.8m x 1.5m"
    }
  },
  "descriptionJson": {
    "descriptions": {
      "en": "This is a sample product description in English",
      "de": "Dies ist eine Beispielproduktbeschreibung auf Deutsch"
    }
  }
}
```

**Response**: The created product with its ID.

### Update a Product

**Endpoint**: `PUT /api/products/{id}`

**Request Body**: Same format as the create request.

**Response**: The updated product.

### Get Product History

**Endpoint**: `GET /api/products/{id}/history`

**Response**: A list of product revisions, each containing:
- Revision number
- Revision timestamp
- Product state at that revision

## Project Structure

- **Main Application**: `EnversFilterDemoApplication.kt`
- **Controllers**: Located in the `controller` package
- **Services**: Located in the `service` package
- **Entities**: Located in the `entity` package
- **Repositories**: Located in the `repo` package
- **DTOs**: Located in the `dto` package

### Key Components

1. **Entities**:
   - `ParentEntity`: The main product entity
   - `TechnicalDetailsContainerEntity`: Contains technical details of a product
   - `DescriptionContainerEntity`: Contains descriptions of a product in multiple languages

2. **Services**:
   - `ProductService`: Handles CRUD operations for products
   - `ProductHistoryService`: Retrieves the revision history of products

3. **Controllers**:
   - `ProductController`: Exposes REST endpoints for product operations

## Testing

The project includes comprehensive integration tests that demonstrate how to test Hibernate Envers functionality.

### Running Tests

```bash
./gradlew test
```

### Test Configuration

Tests use TestContainers to spin up a PostgreSQL container automatically, so there's no need to have a local database running for tests.

The test configuration is defined in:
- `PostgresContainerConfiguration.kt`: Sets up the PostgreSQL test container
- `PostgresIntegrationTest.kt`: Custom annotation that combines Spring Boot test annotations

## Configuration

The application uses the following key configuration properties:

- **Database Configuration**: Located in `application.properties`
  ```properties
  spring.datasource.url=jdbc:postgresql://localhost:5432/pocdb
  spring.datasource.username=user
  spring.datasource.password=password
  ```

- **Hibernate Envers Configuration**:
  ```properties
  spring.jpa.properties.org.hibernate.envers.audit_table_suffix=_AUD
  ```

- **Liquibase Configuration**:
  ```properties
  spring.liquibase.enabled=true
  spring.liquibase.change-log=classpath:/db/changelog/db.changelog-master.xml
  ```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.