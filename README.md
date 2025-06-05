# Event API

A Spring Boot-based REST API for managing events, user connections, and AI-powered quiz generation.

## Features

- User authentication and authorization with JWT
- Event management (creation, subscription, updates)
- User connection system with request/accept workflow
- AI-powered quiz generation using Google's Gemini API
- WebSocket support for real-time notifications
- Secure cookie-based session management
- Role-based access control

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL database
- Google Cloud account (for Gemini API access)

## Configuration

Create an `application.properties` file in `src/main/resources/` with the following properties:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/eventdb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
jwt.secret-key=your_jwt_secret_key
client.id=your_google_client_id

# Gemini API Configuration
gemini.api.key=your_gemini_api_key

# Server Configuration
server.port=8080
```

## Security

- JWT-based authentication
- Secure HTTP-only cookies
- CORS configuration for specific origins
- Password encryption using BCrypt
- Role-based authorization

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/logout` - User logout

### Events
- `POST /api/events` - Create new event
- `GET /api/events` - List all events
- `GET /api/events/{id}` - Get event details
- `PATCH /api/events/{id}` - Update event
- `DELETE /api/events/{id}` - Delete event
- `POST /api/events/subscribe` - Subscribe to event

### Connections
- `POST /api/connections/request` - Request connection
- `GET /api/connections/pending` - Get pending connections
- `PATCH /api/connections/answer` - Answer connection request
- `GET /api/connections/accepted` - Get accepted connections

### Quiz Generation
- `POST /api/quiz/generate` - Generate quiz questions
- `GET /api/quiz/history` - Get quiz history

### WebSocket
- `/websocket` - WebSocket endpoint for real-time notifications
- Topics:
  - `/topic/notifications` - General notifications
  - `/queue/private` - Private messages
  - `/user/queue/notifications` - User-specific notifications

## Error Handling

The API uses standardized error responses with appropriate HTTP status codes:

- 400 Bad Request - Invalid input data
- 401 Unauthorized - Authentication required
- 403 Forbidden - Insufficient permissions
- 404 Not Found - Resource not found
- 409 Conflict - Resource conflict
- 500 Internal Server Error - Server-side error

## Development

1. Clone the repository
2. Configure the application.properties
3. Run `mvn clean install`
4. Start the application with `mvn spring-boot:run`

## Docker Support

Build and run using Docker:

```bash
docker-compose up --build
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

