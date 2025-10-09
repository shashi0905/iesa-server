# IESA Quick Start Guide

Get the IESA application running in **under 5 minutes**! 🚀

## Prerequisites

- ✅ Docker Desktop installed and running
- ✅ Java 17 installed
- ✅ Maven installed

## Step 1: Start Database (30 seconds)

```bash
cd iesa-server
./start-db.sh
```

That's it! PostgreSQL and Redis are now running.

## Step 2: Run Application (2 minutes)

```bash
# Build and start
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Wait for: `Started IesaApplication in X.XX seconds`

## Step 3: Test the API (30 seconds)

### Login as Admin

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "password123"
  }'
```

Copy the `accessToken` from the response.

### Get All Users

```bash
curl -X GET http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## You're Done! 🎉

The application is fully functional with:
- ✅ PostgreSQL database with sample data
- ✅ Redis caching
- ✅ 6 test users
- ✅ 4 roles with 35 permissions
- ✅ JWT authentication
- ✅ 21 REST API endpoints

---

## Available Test Users

| Username | Password | Role | Department |
|----------|----------|------|------------|
| admin | password123 | FINANCE_ADMIN | Finance |
| john.manager | password123 | MANAGER | Engineering |
| jane.marketing | password123 | MANAGER | Marketing |
| alice.dev | password123 | EMPLOYEE | Backend Team |
| bob.dev | password123 | EMPLOYEE | Frontend Team |
| auditor | password123 | AUDITOR | Finance |

---

## Quick Commands

### Database
```bash
# View logs
docker-compose logs -f

# Access database shell
docker exec -it iesa-postgres psql -U postgres -d iesa_dev

# Stop database
./stop-db.sh
```

### Application
```bash
# Run application
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
mvn test

# Package as JAR
mvn package
```

---

## Optional: Database UI

If you started pgAdmin:

**URL:** http://localhost:5050
**Login:** admin@iesa.com / admin123

Server already configured as "IESA Database"

---

## API Endpoints

### Authentication
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh token

### Users (8 endpoints)
- `GET /api/v1/users` - List all users
- `GET /api/v1/users/{id}` - Get user by ID
- `POST /api/v1/users` - Create user
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user
- More...

### Departments (11 endpoints)
- `GET /api/v1/departments` - List all departments
- `GET /api/v1/departments/{id}` - Get department by ID
- `POST /api/v1/departments` - Create department
- More...

---

## Need Help?

📖 **Detailed Documentation:**
- [README.md](README.md) - Complete setup guide
- [DOCKER.md](DOCKER.md) - Docker documentation
- [API Examples](README.md#api-usage-examples) - More API examples

🐛 **Troubleshooting:**
- Check Docker is running: `docker info`
- Check logs: `docker-compose logs -f`
- Reset database: `docker-compose down -v && docker-compose up -d`

---

## What's Next?

1. Explore API endpoints with Postman or curl
2. Check database schema in pgAdmin
3. Create new users and departments
4. Review the code structure
5. Start building new features!

**Happy coding! 🚀**
