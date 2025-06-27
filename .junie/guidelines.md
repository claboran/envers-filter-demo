# Envers Filter Demo Project Guidelines

This document provides guidelines for developing and working with the Envers Filter Demo project.

## Build and Configuration Instructions

### Prerequisites
- Java 21 or higher
- Docker and Docker Compose

### Setting Up the Development Environment

1. **Start the PostgreSQL Database**:
   ```bash
   cd iac
   docker-compose up -d
   ```
   This will start a PostgreSQL 16 container with the following configuration:
   - Database: pocdb
   - Username: user
   - Password: password
   - Port: 5432

2. **Build the Project**:
   ```bash
   ./gradlew build
   ```

3. **Run the Application**:
   ```bash
   ./gradlew bootRun
   ```

### Configuration

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

## Testing Information

### Test Configuration

Tests use TestContainers to spin up a PostgreSQL container automatically, so there's no need to have a local database running for tests.

The test configuration is defined in:
- `PostgresContainerConfiguration.kt`: Sets up the PostgreSQL test container
- `PostgresIntegrationTest.kt`: Custom annotation that combines Spring Boot test annotations

### Running Tests

To run all tests:
```bash
./gradlew test
```

To run a specific test class:
```bash
./gradlew test --tests "de.laboranowitsch.poc.enversfilterdemo.service.ProductServiceIntegrationTest"
```

### Writing Tests

1. **Integration Tests**:
   - Use the `@PostgresIntegrationTest` annotation to set up the test container
   - Use `@SpringBootTest` to load the full application context
   - Use `@Transactional` to ensure database cleanup after each test

   Example:
   ```kotlin
   @SpringBootTest
   @PostgresIntegrationTest
   class MyIntegrationTest {
       @Test
       fun `test something`() {
           // Test code here
       }
   }
   ```

2. **Test Logging**:
   - Use `println("[DEBUG_LOG] Your message")` to add debug logs to your tests

## Development Information

### Project Structure

- **Main Application**: `EnversFilterDemoApplication.kt`
- **Controllers**: Located in the `controller` package
- **Services**: Located in the `service` package
- **Entities**: Located in the `entity` package
- **Repositories**: Located in the `repo` package
- **DTOs**: Located in the `dto` package

### Key Components

1. **Hibernate Envers**:
   - Used for entity auditing
   - Audit tables have the suffix `_AUD`
   - The `ProductHistoryService` provides methods to access entity history

2. **Liquibase**:
   - Used for database schema management
   - Changelog is located at `src/main/resources/db/changelog/db.changelog-master.xml`

### Code Style

- The project uses Kotlin with Spring Boot
- JPA entities use the `@Entity` annotation and must be open classes (handled by the Kotlin JPA plugin)
- Use the `allOpen` plugin configuration for JPA entities:
  ```kotlin
  allOpen {
      annotation("jakarta.persistence.Entity")
      annotation("jakarta.persistence.MappedSuperclass")
      annotation("jakarta.persistence.Embeddable")
  }
  ```

### API Testing

The project includes HTTP request examples in `src/test/resources/http/rest-api.http` that can be used with IntelliJ's HTTP Client or similar tools.
