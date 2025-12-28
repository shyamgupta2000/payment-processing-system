# 💰 Payment Processing System

A robust, enterprise-grade payment processing system built with Spring Boot, MySQL, and ActiveMQ for asynchronous message processing.

## 🏗️ Architecture

```
┌─────────────┐      ┌──────────────┐      ┌──────────────┐
│   REST API  │─────▶│  Service     │─────▶│  Repository  │
│ (Controller)│      │  Layer       │      │   (JPA)      │
└─────────────┘      └──────────────┘      └──────────────┘
       │                    │                       │
       │                    ▼                       ▼
       │            ┌──────────────┐        ┌──────────────┐
       │            │ JMS Producer │        │    MySQL     │
       │            └──────────────┘        └──────────────┘
       │                    │
       ▼                    ▼
┌─────────────┐      ┌──────────────┐
│  Exception  │      │   ActiveMQ   │
│  Handler    │      │   Queues     │
└─────────────┘      └──────────────┘
                            │
                            ▼
                     ┌──────────────┐
                     │ JMS Consumer │
                     │ (Async Proc) │
                     └──────────────┘
```

## 🚀 Features

- ✅ **RESTful APIs** - 9 endpoints for complete CRUD operations
- ✅ **Asynchronous Processing** - JMS/ActiveMQ with 3 message queues
- ✅ **Database Persistence** - MySQL with JPA/Hibernate
- ✅ **Validation** - Bean validation with custom constraints
- ✅ **Exception Handling** - Global exception handler
- ✅ **Logging** - SLF4J with structured logging
- ✅ **Transaction Management** - ACID compliance
- ✅ **Health Monitoring** - Spring Boot Actuator
- ✅ **Containerization** - Docker & Docker Compose
- ✅ **Unit Testing** - JUnit 5 with Mockito

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6. 3+
- MySQL 8.0+
- ActiveMQ 5.18.3+
- Docker & Docker Compose (optional)

## ⚙️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming Language |
| Spring Boot | 3.5.x | Application Framework |
| Spring Data JPA | 3.5.x | Data Access Layer |
| MySQL | 8.0 | Database |
| ActiveMQ | 5.18.3 | Message Broker |
| Lombok | Latest | Boilerplate Reduction |
| JUnit 5 | Latest | Unit Testing |
| Docker | Latest | Containerization |

## 🛠️ Installation & Setup

### Method 1: Local Setup

#### 1. Clone the repository
```bash
git clone <repository-url>
cd payment-processing-system
```

#### 2. Configure MySQL
```bash
sudo mysql -u root -p
```

```sql
CREATE DATABASE payment_db;
CREATE USER 'shyam_user'@'localhost' IDENTIFIED BY 'Shyam@123';
GRANT ALL PRIVILEGES ON payment_db.* TO 'shyam_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

#### 3. Start ActiveMQ
```bash
cd /opt/activemq
./bin/activemq start
```

#### 4. Build and Run
```bash
./mvnw clean install -DskipTests
./mvnw spring-boot:run
```

### Method 2: Docker Compose

```bash
# Build the application
./mvnw clean package -DskipTests

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f payment-app

# Stop all services
docker-compose down
```

## 📡 API Endpoints

### Base URL: `http://localhost:8080/api/payments`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create new payment |
| GET | `/` | Get all payments |
| GET | `/{id}` | Get payment by ID |
| GET | `/transaction/{txnId}` | Get payment by transaction ID |
| GET | `/sender/{account}` | Get payments by sender |
| GET | `/receiver/{account}` | Get payments by receiver |
| GET | `/status/{status}` | Get payments by status |
| PATCH | `/{id}/status? status={status}` | Update payment status |
| DELETE | `/{id}` | Delete payment |

## 🧪 API Testing Examples

### Create Payment
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type:  application/json" \
  -d '{
    "senderAccount": "1234567890",
    "receiverAccount": "0987654321",
    "amount": 5000.00,
    "currency": "INR",
    "paymentMethod": "UPI",
    "description": "Payment for services"
  }'
```

### Get All Payments
```bash
curl http://localhost:8080/api/payments
```

### Get Payment by ID
```bash
curl http://localhost:8080/api/payments/1
```

### Update Payment Status
```bash
curl -X PATCH "http://localhost:8080/api/payments/1/status?status=COMPLETED"
```

## 📊 Payment Status Flow

```
PENDING → PROCESSING → COMPLETED
                    ↘
                      FAILED
                    ↗
              CANCELLED
```

## 🔐 Security Features

- Input validation using Bean Validation
- SQL injection prevention via JPA
- Transaction management for data consistency
- Exception handling for security

## 🏥 Health Monitoring

### Actuator Endpoints

```bash
# Health check
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics

# Application info
curl http://localhost:8080/actuator/info
```

## 📨 JMS Queues

| Queue Name | Purpose |
|------------|---------|
| `payment.queue` | Initial payment submission |
| `payment.processing.queue` | Async payment processing |
| `payment.notification.queue` | Payment notifications |

### ActiveMQ Console
- URL: http://localhost:8161/admin
- Username: `admin`
- Password: `admin`

## 🗄️ Database Schema

```sql
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(50) UNIQUE NOT NULL,
    sender_account VARCHAR(20) NOT NULL,
    receiver_account VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status ENUM('PENDING','PROCESSING','COMPLETED','FAILED','CANCELLED'),
    payment_method VARCHAR(50),
    description VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);
```

## 🧪 Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=PaymentServiceTest

# Run with coverage
./mvnw clean test jacoco:report
```

## 📦 Project Structure

```
src/
├── main/
│   ├── java/com/banking/payment/
│   │   ├── config/           # Configuration classes
│   │   ├── controller/       # REST controllers
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── exception/        # Exception handling
│   │   ├── messaging/        # JMS producers/consumers
│   │   ├── model/            # JPA entities
│   │   ├── repository/       # Data repositories
│   │   └── service/          # Business logic
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/banking/payment/
        └── PaymentServiceTest.java
```

## 🐛 Troubleshooting

### MySQL Connection Issues
```bash
# Check MySQL status
sudo systemctl status mysql

# Test connection
mysql -u shyam_user -p"Shyam@123" payment_db
```

### ActiveMQ Issues
```bash
# Check ActiveMQ status
sudo systemctl status activemq

# Check logs
tail -f /opt/activemq/data/activemq.log
```

### Application Issues
```bash
# Check if port 8080 is in use
sudo lsof -i :8080

# View application logs
tail -f logs/application.log
```

## 🚀 Deployment

### Build JAR
```bash
./mvnw clean package -DskipTests
```

### Build Docker Image
```bash
docker build -t payment-processing-system: 1.0.0 .
```

### Run Docker Container
```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host. docker.internal:3306/payment_db \
  payment-processing-system:1.0.0
```

## 📈 Performance

- **Concurrent Processing**: 3-10 JMS consumers
- **Connection Pooling**: HikariCP
- **Transaction Isolation**: READ_COMMITTED
- **Response Time**: < 100ms (avg)

## 🔄 Future Enhancements

- [ ] Add Spring Security (JWT authentication)
- [ ] Implement Redis caching
- [ ] Add Kafka for event streaming
- [ ] Implement circuit breaker pattern
- [ ] Add API rate limiting
- [ ] Implement payment reconciliation
- [ ] Add fraud detection module
- [ ] Implement webhook notifications

## 👨‍💻 Developer

**Shyam Gupta**
- GitHub: @shyamgupta2000
- Email: shyamgupta2000@example.com

## 📄 License

This project is licensed under the MIT License.

## 🙏 Acknowledgments

- Spring Boot Team
- Apache ActiveMQ Community
- MySQL Community

---

**Built with ❤️ using Spring Boot**