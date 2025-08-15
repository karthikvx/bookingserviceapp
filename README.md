# Booking MVC Application

A Spring Boot MVC application with Thymeleaf frontend and Kafka integration for managing bookings with duplicate prevention capabilities.

## 🚀 Features

- **Duplicate Prevention**: Prevents duplicate bookings for the same user, resource, and time slot
- **REST APIs**: JSON-based APIs for external integration
- **Web Interface**: Responsive Thymeleaf-based UI with Bootstrap styling
- **Real-time Events**: Kafka integration for booking lifecycle events
- **Transaction Management**: Ensures data consistency across operations
- **Comprehensive Validation**: Input validation with meaningful error messages
- **virtual thread enabled**
- **throttling filter added**

## 🛠 Technology Stack

- **Backend**: Spring Boot 3.2.0, Spring MVC, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **Database**: H2 (development) / MySQL / PostgreSQL (production)
- **Message Queue**: Apache Kafka
- **Build Tool**: gradle 8.14.2
- **Java Version**: 21

## 📋 Prerequisites

- Java 24 or higher
- gradle 8.14.2
- Docker and Docker Compose (for Kafka)

## ⚡ Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd booking-mvc-app
```

### 2. Start Kafka Infrastructure
```bash
docker-compose up -d
```

### 3. Run the Application
```bash
# Development mode with sample data
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Or standard mode
mvn spring-boot:run
```

### 4. Access the Application

| Service | URL | Description |
|---------|-----|-------------|
| Web Interface | http://localhost:8080/bookings | Main booking management UI |
| H2 Console | http://localhost:8080/h2-console | Database console (JDBC URL: `jdbc:h2:mem:bookingdb`) |
| Kafka UI | http://localhost:8081 | Kafka cluster management |

## 🌐 API Endpoints

### REST APIs

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `POST` | `/api/bookings/add` | Create new booking | `BookingRequest` |
| `PUT` | `/api/bookings/cancel/{id}` | Cancel existing booking | None |
| `GET` | `/api/bookings/all` | Get all active bookings | None |
| `GET` | `/api/bookings/user/{userId}` | Get user's bookings | None |

### Web Pages

| Route | Description |
|-------|-------------|
| `GET /bookings` | Main booking management page |
| `GET /bookings/user/{userId}` | User-specific bookings view |

## 📝 API Usage Examples

### Add New Booking
```bash
curl -X POST http://localhost:8080/api/bookings/add \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "john.doe",
    "resourceId": "meeting-room-1",
    "bookingDate": "2024-12-25T14:30:00",
    "notes": "Team planning session"
  }'
```

**Response:**
```json
{
  "id": 1,
  "userId": "john.doe",
  "resourceId": "meeting-room-1",
  "bookingDate": "2024-12-25T14:30:00",
  "status": "ACTIVE",
  "createdAt": "2024-08-14T10:00:00",
  "message": "Booking created successfully"
}
```

### Cancel Booking
```bash
curl -X PUT http://localhost:8080/api/bookings/cancel/1
```

### Get All Active Bookings
```bash
curl http://localhost:8080/api/bookings/all
```

### Get User Bookings
```bash
curl http://localhost:8080/api/bookings/user/john.doe
```

## 🔒 Duplicate Prevention

The system prevents duplicate bookings using:

- **Database Constraints**: Unique constraint on (`user_id`, `resource_id`, `booking_date`)
- **Service Layer Validation**: Pre-insertion checks for active bookings
- **Transaction Management**: Ensures atomicity of operations

### Duplicate Booking Response
```json
{
  "status": 409,
  "message": "Booking already exists for user john.doe and resource meeting-room-1 at 2024-12-25T14:30:00",
  "timestamp": "2024-08-14T15:30:00"
}
```

## 📊 Database Schema

### Bookings Table
```sql
CREATE TABLE bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(255) NOT NULL,
    resource_id VARCHAR(255) NOT NULL,
    booking_date DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    UNIQUE KEY unique_booking (user_id, resource_id, booking_date)
);
```

## 🔄 Kafka Integration

### Topics
- **Topic**: `booking-events`
- **Partitions**: 3
- **Replication Factor**: 1

### Event Types
- `booking.created` - Published when a new booking is created
- `booking.cancelled` - Published when a booking is cancelled

### Monitor Kafka Events
```bash
# View Kafka topics
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list

# Consume booking events
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic booking-events \
  --from-beginning
```

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Test Categories
- **Unit Tests**: Service and controller logic
- **Integration Tests**: End-to-end functionality with embedded Kafka
- **Web Tests**: MockMvc-based controller testing

### Test Coverage
- Service layer duplicate prevention
- Controller request/response handling
- Kafka event publishing
- Database operations
- Validation scenarios

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/booking/
│   │   ├── BookingApplication.java
│   │   ├── config/
│   │   │   ├── KafkaConfig.java
│   │   │   └── DataLoader.java
│   │   ├── controller/
│   │   │   ├── BookingRestController.java
│   │   │   └── BookingController.java
│   │   ├── dto/
│   │   │   ├── BookingRequest.java
│   │   │   └── BookingResponse.java
│   │   ├── entity/
│   │   │   └── Booking.java
│   │   ├── exception/
│   │   │   ├── DuplicateBookingException.java
│   │   │   ├── BookingNotFoundException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── repository/
│   │   │   └── BookingRepository.java
│   │   └── service/
│   │       ├── BookingService.java
│   │       └── BookingEventConsumer.java
│   └── resources/
│       ├── templates/
│       │   ├── layout/base.html
│       │   ├── bookings/list.html
│       │   ├── bookings/user-bookings.html
│       │   └── error.html
│       └── application.yml
├── test/
└── docker-compose.yml
```

## ⚙️ Configuration

### Application Profiles

#### Development (`application-dev.yml`)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:bookingdb
  jpa:
    show-sql: true
  kafka:
    bootstrap-servers: localhost:9092
```

#### Production (`application-prod.yml`)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bookingdb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  kafka:
    bootstrap-servers: ${KAFKA_BROKERS}
```

## 🚀 Production Deployment

### 1. Database Setup
```bash
# PostgreSQL example
docker run -d \
  --name postgres \
  -e POSTGRES_DB=bookingdb \
  -e POSTGRES_USER=booking \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  postgres:14
```

### 2. Application Deployment
```bash
# Build application
mvn clean package -DskipTests

# Run with production profile
java -jar target/booking-mvc-app-1.0.0.jar --spring.profiles.active=prod
```

### 3. Environment Variables
```bash
export DB_USERNAME=booking
export DB_PASSWORD=password
export KAFKA_BROKERS=kafka-cluster:9092
```

## 🔧 Configuration Options

### Key Properties
```yaml
# Server configuration
server:
  port: 8080

# Database configuration
spring:
  datasource:
    url: jdbc:h2:mem:bookingdb
    username: sa
    password: 

# Kafka configuration
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: booking-group
```

## 📈 Monitoring and Observability

### Health Checks
- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information

### Kafka Monitoring
- Use Kafka UI at http://localhost:8081
- Monitor topic `booking-events` for message flow

### Database Monitoring
- H2 Console: http://localhost:8080/h2-console
- Connection String: `jdbc:h2:mem:bookingdb`

## 🐛 Troubleshooting

### Common Issues

#### Kafka Connection Issues
```bash
# Check Kafka container status
docker-compose ps

# Restart Kafka services
docker-compose restart kafka zookeeper
```

#### Database Connection Issues
```bash
# Verify H2 console access
# URL: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:bookingdb
# Username: sa
# Password: (empty)
```

#### Port Conflicts
```yaml
# Change application port in application.yml
server:
  port: 8090
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Write tests for new features
- Follow existing code style
- Update documentation for API changes
- Ensure all tests pass before submitting PR

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Apache Kafka for reliable event streaming
- Bootstrap for responsive UI components
- H2 Database for development simplicity

---

**For support or questions, please open an issue in the GitHub repository.**