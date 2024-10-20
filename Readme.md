# Rental Management API

This project is a **REST API** developed in **Java** using the **Spring Boot** framework. The API is designed for managing temporary property rentals. It provides functionalities for property registration, listing, editing, and logical deletion of properties. Additionally, users can rent available properties.

The API uses **JWT (JSON Web Tokens)** for secure authentication, allowing users to register and log in to the platform.

## Features
- **Property Management**: Register, list, edit, and delete properties (logical deletion).
- **User Authentication**: Sign-up and sign-in using JWT tokens.
- **Property Rentals**: Allows users to rent properties after authentication.

## Endpoints

### Session Endpoints
- `POST /session/signup`: Register a new user.
- `POST /session/signin`: Authenticate and generate a JWT token for a user.

### Properties Endpoints
- `GET /properties`: List available properties with optional price filters.
- `POST /properties`: Register a new property.
- `PUT /properties/{id}`: Edit a property.
- `DELETE /properties/{id}`: Perform a logical deletion of a property.
- `POST /properties/rent/{id}`: Rent a property (requires authentication).


## Build and Test Instructions

### Prerequisites
- **Java 17** is required.
- **Maven** must be installed.
- A local **PostgreSQL** database running on port `5432` with the following credentials:
  - **Database Name**: `property_test`
  - **Username**: `prueba`
  - **Password**: `pruebapass`

### Building the Application

To build the project, run the following Maven command:

```bash
mvn clean package -DskipTests
```

> **Note**: The `-DskipTests` flag can be removed to run tests during the build process. Ensure the database is empty when running tests.

### Running the Application

You can run the application using the following command:

```bash
java -jar -Dspring.profiles.active=local ./infrastructure/target/infrastructure-0.0.1-SNAPSHOT.jar
```

By default, the application will start on port `8080`.

### Running with Docker Compose

Alternatively, you can run the application using Docker Compose:
1. Copy the example environment file:

```bash
cp .env.example .env
```

Modify the variables in the `.env` file if needed (default values are the same as the local environment):

```bash
DB_USER=prueba
DB_PASSWORD=pruebapass
DB_NAME=property_test
PORT=8080
```

2. Start the application using Docker Compose:

```bash
docker compose --env-file=.env up
```

### Running Tests and Generating Reports

The project includes **Jacoco** for test coverage reporting. To generate the reports, first ensure the database is set up correctly as described earlier. Then, run the following Maven command:

```bash
mvn clean verify -Pjacoco-report
```

Reports will be generated inside the `target/surefire-reports/` directory of each submodule.

### Project Architecture

This project follows a **hexagonal architecture** with three main submodules:
- **Domain**: Core business logic.
- **Application**: Handles application logic and use cases.
- **Infrastructure**: External components such as databases, web, and configurations.