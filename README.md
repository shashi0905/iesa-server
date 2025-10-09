# IESA Server - User & Role Management Module

Invoice and Expense Segmentation App (IESA) Backend - User Management Module Implementation

## Overview

This is the backend server for the Invoice and Expense Segmentation App (IESA), implementing the **User & Role Management** module as the first module of a modular monolith architecture.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **PostgreSQL 15+** (Production)
- **H2 Database** (Testing)
- **Spring Security** with JWT authentication
- **Spring Data JPA** with Hibernate
- **MapStruct** for DTO mapping
- **Flyway** for database migrations
- **Redis** for caching
- **Maven** for build management

## Architecture

**Modular Monolith** with clear module boundaries:
- User Management ✅ (Implemented)
- Segment Management (Pending)
- Expense Management (Pending)
- Approval Workflow (Pending)
- Budget Management (Pending)
- Reporting & Analytics (Pending)
- Notification Service (Pending)
- Integration Layer (Pending)

## Features Implemented

### User & Role Management Module

1. **User Management**
   - Create, read, update, delete users
   - User authentication with JWT
   - Account locking/unlocking
   - Password management
   - Failed login attempt tracking

2. **Role Management**
   - Predefined roles: EMPLOYEE, MANAGER, FINANCE_ADMIN, AUDITOR
   - Role-based access control (RBAC)
   - Permission-based authorization

3. **Department Management**
   - Hierarchical department structure
   - Department CRUD operations
   - Manager assignment

4. **Security**
   - JWT-based authentication
   - BCrypt password hashing
   - Method-level security with @PreAuthorize
   - CORS configuration
   - Soft delete for audit compliance

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker and Docker Compose (recommended)
- **OR** PostgreSQL 15+ (if not using Docker)
- Redis 7+ (optional, for caching)

## Setup Instructions

### Option 1: Using Docker (Recommended) 🐳

Docker setup provides PostgreSQL, Redis, and optionally pgAdmin with zero manual configuration.

#### Quick Start

```bash
# 1. Start database services using the helper script
./start-db.sh

# 2. Build and run the application
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The `start-db.sh` script will:
- ✅ Start PostgreSQL on port 5432
- ✅ Start Redis on port 6379
- ✅ Optionally start pgAdmin on port 5050
- ✅ Initialize database with required extensions
- ✅ Create database schema (via Flyway when app starts)

**Stop services:**
```bash
./stop-db.sh
```

**View logs:**
```bash
docker-compose logs -f
```

**Access database shell:**
```bash
docker exec -it iesa-postgres psql -U postgres -d iesa_dev
```

**pgAdmin (Database UI):**
- URL: http://localhost:5050
- Email: admin@iesa.com
- Password: admin123

📖 **For detailed Docker documentation, see [DOCKER.md](DOCKER.md)**

### Option 2: Manual PostgreSQL Setup

#### For Development (PostgreSQL):

```bash
# Create database
createdb iesa_dev

# Update credentials in application.yml if needed
DB_HOST=localhost
DB_PORT=5432
DB_NAME=iesa_dev
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

#### For Testing (H2 - In-Memory):

No setup needed. H2 database is automatically configured for the `test` profile.

### 2. Redis Setup (Optional)

```bash
# Start Redis (if using caching)
redis-server

# Or using Docker
docker run -d -p 6379:6379 redis:7-alpine
```

### 3. Build the Application

```bash
cd iesa-server
mvn clean install
```

### 4. Run the Application

```bash
# Run with dev profile (PostgreSQL)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run with test profile (H2)
mvn spring-boot:run -Dspring-boot.run.profiles=test

# Or run the JAR
java -jar target/iesa-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

The application will start on `http://localhost:8080`

## Default Users

The application comes with sample users (password: `password123`):

| Username | Email | Role | Department |
|----------|-------|------|------------|
| admin | admin@company.com | FINANCE_ADMIN | Finance |
| john.manager | john.manager@company.com | MANAGER | Engineering |
| jane.marketing | jane.marketing@company.com | MANAGER | Marketing |
| alice.dev | alice.dev@company.com | EMPLOYEE | Backend Team |
| bob.dev | bob.dev@company.com | EMPLOYEE | Frontend Team |
| auditor | auditor@company.com | AUDITOR | Finance |

## API Endpoints

### Authentication

```
POST /api/v1/auth/login
POST /api/v1/auth/refresh
```

### Users

```
GET    /api/v1/users                  - Get all users
GET    /api/v1/users/{id}             - Get user by ID
GET    /api/v1/users/department/{id}  - Get users by department
GET    /api/v1/users/active           - Get active users
POST   /api/v1/users                  - Create user
PUT    /api/v1/users/{id}             - Update user
DELETE /api/v1/users/{id}             - Delete user (soft delete)
POST   /api/v1/users/{id}/lock        - Lock user account
POST   /api/v1/users/{id}/unlock      - Unlock user account
```

### Departments

```
GET    /api/v1/departments              - Get all departments
GET    /api/v1/departments/{id}         - Get department by ID
GET    /api/v1/departments/root         - Get root departments
GET    /api/v1/departments/{id}/children - Get child departments
GET    /api/v1/departments/active       - Get active departments
POST   /api/v1/departments              - Create department
PUT    /api/v1/departments/{id}         - Update department
DELETE /api/v1/departments/{id}         - Delete department (soft delete)
POST   /api/v1/departments/{id}/activate - Activate department
POST   /api/v1/departments/{id}/deactivate - Deactivate department
```

## API Usage Examples

### 1. Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "password123"
  }'
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900000,
  "user": {
    "id": "650e8400-e29b-41d4-a716-446655440001",
    "username": "admin",
    "email": "admin@company.com",
    "firstName": "Admin",
    "lastName": "User",
    "roles": [...]
  }
}
```

### 2. Get All Users (Authenticated)

```bash
curl -X GET http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 3. Create User

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "new.user",
    "email": "new.user@company.com",
    "password": "password123",
    "firstName": "New",
    "lastName": "User",
    "phoneNumber": "+1234567896",
    "departmentId": "550e8400-e29b-41d4-a716-446655440001",
    "roleIds": ["ROLE_ID_HERE"]
  }'
```

### 4. Create Department

```bash
curl -X POST http://localhost:8080/api/v1/departments \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Operations",
    "code": "OPS",
    "description": "Operations Department",
    "costCenter": "CC-OPS-001"
  }'
```

## Configuration

### Application Properties

Key configuration options in `application.yml`:

```yaml
iesa:
  jwt:
    secret: your-secret-key-here
    access-token-expiration: 900000  # 15 minutes
    refresh-token-expiration: 604800000  # 7 days

  security:
    cors:
      allowed-origins: http://localhost:3000,http://localhost:5173

  file-storage:
    type: local
    local:
      base-path: ./uploads
```

### Database Migration

Flyway migrations are located in `src/main/resources/db/migration/`:

- `V1__create_user_management_tables.sql` - Schema creation
- `V2__insert_default_permissions_and_roles.sql` - Default data
- `V3__insert_sample_data.sql` - Sample users and departments

## Security

### Roles & Permissions

#### EMPLOYEE Role
- Can submit own expenses
- Can read own expenses
- Can view segments and departments

#### MANAGER Role
- All EMPLOYEE permissions
- Can approve department expenses
- Can view department reports

#### FINANCE_ADMIN Role
- Full system access
- Can manage users, departments, budgets
- Can approve all expenses

#### AUDITOR Role
- Read-only access to all data
- Can export reports
- For compliance purposes

### Authentication Flow

1. User logs in with username/email and password
2. Server validates credentials and returns JWT tokens (access + refresh)
3. Client includes access token in Authorization header: `Bearer TOKEN`
4. Token is validated on each request
5. Access token expires in 15 minutes
6. Refresh token used to obtain new access token (expires in 7 days)

## Development

### Project Structure

```
src/main/java/com/company/iesa/
├── IesaApplication.java              # Main application class
├── shared/                            # Shared kernel
│   ├── config/                        # Security, JWT config
│   ├── domain/                        # Base entities
│   ├── exception/                     # Global exception handling
│   └── util/                          # Utilities
└── usermanagement/                    # User Management module
    ├── controller/                    # REST controllers
    ├── domain/                        # Entities (User, Role, etc.)
    ├── dto/                           # DTOs and mappers
    ├── repository/                    # JPA repositories
    ├── security/                      # Security services
    └── service/                       # Business logic
```

### Adding New Modules

To add a new module (e.g., Expense Management):

1. Create package: `com.company.iesa.expensemanagement`
2. Follow same structure: `domain`, `repository`, `service`, `dto`, `controller`
3. Create Flyway migration for new tables
4. Add module-specific configuration if needed

## Monitoring

### Actuator Endpoints

```
GET /actuator/health    - Health check
GET /actuator/info      - Application info
GET /actuator/metrics   - Metrics
```

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report
```

## Troubleshooting

### Common Issues

1. **Database connection error**
   - Check PostgreSQL is running
   - Verify database credentials in `application.yml`
   - Ensure database `iesa_dev` exists

2. **JWT token error**
   - Ensure JWT secret is at least 32 characters
   - Check token hasn't expired
   - Verify Authorization header format: `Bearer TOKEN`

3. **Port already in use**
   - Change port in `application.yml`: `server.port=8081`
   - Or stop the process using port 8080

## Next Steps

The following modules are planned for implementation:

1. ✅ User & Role Management (Completed)
2. ⏳ Segment Management (Next)
3. ⏳ Expense Management
4. ⏳ Approval Workflow
5. ⏳ Budget Management
6. ⏳ Reporting & Analytics
7. ⏳ Notification Service
8. ⏳ Integration Layer

## License

Proprietary - Company Internal Use Only

## Contact

For questions or support, contact the development team.
