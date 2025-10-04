# Design Patterns

A Spring Boot application demonstrating the **Combinator Pattern** for user validation.

## Overview

This project showcases functional composition patterns in Java, specifically implementing a combinator-based validation system for user data.

## Key Features

- **Combinator Pattern**: Composable validation rules using `and()`, `or()`, and `when()` combinators
- **Functional Validation**: Type-safe, reusable validation components
- **Spring Boot REST API**: User management endpoints with validation
- **PostgreSQL Integration**: Data persistence with JPA
- **Docker Support**: Docker Compose configuration included

## Tech Stack

- Java 25
- Spring Boot 3.5.6
- PostgreSQL
- Maven

## Combinator Pattern Implementation

The validation system uses functional composition to build complex validation rules:

```java
// Combine validations with AND
Validation<UserDto> all() {
  return isFirstNameValid()
      .and(isLastNameValid())
      .and(isEmailValid())
      .and(isAgeValid());
}

// Conditional validation
Validation<UserDto> whenAdult = Validation.when(
  user.age() >= 18,
  isPhoneValid()
);
```

### Available Validators

- Email validation (regex-based)
- Name validation (first/last name)
- Age validation (18-120, optional)
- Phone validation (optional)
- Address validation (optional)
- Minimum name length validation

## Running the Application

```bash
# Start with Docker Compose
docker-compose up

# Or build and run with Maven
mvn clean install
mvn spring-boot:run
```

## Project Structure

```
src/main/java/org/java/
├── user/
│   ├── controllers/     # REST controllers
│   ├── data/            # JPA repositories
│   ├── dto/             # Data transfer objects
│   ├── exception/       # Exception handling
│   ├── model/           # Entity models
│   ├── service/         # Business logic
│   └── validation/      # Combinator pattern implementation
```

## License

This is a demonstration project for educational purposes.
