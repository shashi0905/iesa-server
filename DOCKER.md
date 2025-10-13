# Docker Setup Guide for IESA

This guide explains how to set up and run PostgreSQL database using Docker for the IESA application.

## Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+

### Install Docker

**macOS:**
```bash
brew install --cask docker
# Or download from: https://www.docker.com/products/docker-desktop
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get update
sudo apt-get install docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
```

**Windows:**
Download and install Docker Desktop from https://www.docker.com/products/docker-desktop

### Verify Installation

```bash
docker --version
docker-compose --version
```

## Docker Services

The Docker Compose configuration provides three services:

1. **PostgreSQL 15** - Main database (Port 5432)
2. **Redis 7** - Cache server (Port 6379)
3. **pgAdmin 4** - Database management UI (Port 5050) - Optional

## Quick Start

### 1. Configuration

Copy the example environment file and customize if needed:

```bash
cd iesa-server
cp .env.example .env
```

Edit `.env` file with your preferred settings:

```bash
# PostgreSQL Database Configuration
DB_NAME=iesa_dev
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_PORT=5432

# Redis Configuration
REDIS_PORT=6379
REDIS_PASSWORD=

# pgAdmin Configuration
PGADMIN_EMAIL=admin@iesa.com
PGADMIN_PASSWORD=admin123
PGADMIN_PORT=5050
```

### 2. Start Services

**Start PostgreSQL and Redis only:**
```bash
docker-compose up -d
```

**Start with pgAdmin (Database UI):**
```bash
docker-compose --profile tools up -d
```

**View logs:**
```bash
docker-compose logs -f
```

### 3. Verify Services are Running

```bash
docker-compose ps
```

Expected output:
```
NAME                IMAGE                   STATUS          PORTS
iesa-postgres       postgres:15-alpine      Up 10 seconds   0.0.0.0:5432->5432/tcp
iesa-redis          redis:7-alpine          Up 10 seconds   0.0.0.0:6379->6379/tcp
iesa-pgadmin        dpage/pgadmin4:latest   Up 10 seconds   0.0.0.0:5050->80/tcp
```

### 4. Test Database Connection

**Using psql (inside Docker):**
```bash
docker exec -it iesa-postgres psql -U postgres -d iesa_dev
```

**Using psql (from host):**
```bash
psql -h localhost -p 5432 -U postgres -d iesa_dev
```

**Test Query:**
```sql
SELECT version();
\l
\q
```

### 5. Run Spring Boot Application

The application will now connect to the Dockerized database:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Or update `application.yml` to use environment variables:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:iesa_dev}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
```

## Docker Commands

### Starting Services

```bash
# Start all services in background
docker-compose up -d

# Start specific service
docker-compose up -d postgres

# Start with logs
docker-compose up
```

### Stopping Services

```bash
# Stop all services
docker-compose stop

# Stop specific service
docker-compose stop postgres

# Stop and remove containers
docker-compose down
```

### Viewing Logs

```bash
# View all logs
docker-compose logs

# Follow logs
docker-compose logs -f

# View specific service logs
docker-compose logs postgres
docker-compose logs redis

# Last 100 lines
docker-compose logs --tail=100 postgres
```

### Restart Services

```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart postgres
```

### Accessing Containers

```bash
# PostgreSQL shell
docker exec -it iesa-postgres psql -U postgres -d iesa_dev

# Redis CLI
docker exec -it iesa-redis redis-cli

# Bash shell in PostgreSQL container
docker exec -it iesa-postgres bash

# Bash shell in Redis container
docker exec -it iesa-redis sh
```

## pgAdmin - Database Management UI

If you started pgAdmin with `--profile tools`, you can access it at:

**URL:** http://localhost:5050

**Login:**
- Email: `admin@iesa.com` (or value from .env)
- Password: `admin123` (or value from .env)

**Connect to Database:**
1. Click "Add New Server"
2. General Tab:
   - Name: `IESA Database`
3. Connection Tab:
   - Host: `postgres` (container name)
   - Port: `5432`
   - Database: `iesa_dev`
   - Username: `postgres`
   - Password: `postgres`
4. Click "Save"

The server is pre-configured in `docker/pgadmin/servers.json`, but you may need to enter the password.

## Data Persistence

Docker volumes are used for data persistence:

```bash
# List volumes
docker volume ls

# Inspect volume
docker volume inspect iesa-server_postgres-data
docker volume inspect iesa-server_redis-data

# Volume locations:
# - postgres-data: /var/lib/postgresql/data
# - redis-data: /data
# - pgadmin-data: /var/lib/pgadmin
```

## Database Initialization

The initialization script `docker/postgres/init/01-init-database.sh` runs automatically when the PostgreSQL container is first created.

It performs:
- Creates UUID extension
- Creates pg_trgm extension (for text search)
- Sets timezone to UTC
- Grants privileges

**Note:** This script only runs on first container creation. If you need to re-run:

```bash
# Remove volumes and restart
docker-compose down -v
docker-compose up -d
```

## Backup and Restore

### Backup Database

```bash
# Backup to file
docker exec iesa-postgres pg_dump -U postgres iesa_dev > backup.sql

# Backup with compression
docker exec iesa-postgres pg_dump -U postgres iesa_dev | gzip > backup.sql.gz

# Backup using docker-compose
docker-compose exec postgres pg_dump -U postgres iesa_dev > backup.sql
```

### Restore Database

```bash
# Restore from file
docker exec -i iesa-postgres psql -U postgres iesa_dev < backup.sql

# Restore from compressed file
gunzip -c backup.sql.gz | docker exec -i iesa-postgres psql -U postgres iesa_dev

# Drop and recreate database before restore
docker exec iesa-postgres psql -U postgres -c "DROP DATABASE IF EXISTS iesa_dev;"
docker exec iesa-postgres psql -U postgres -c "CREATE DATABASE iesa_dev;"
docker exec -i iesa-postgres psql -U postgres iesa_dev < backup.sql
```

### Automated Backup Script

Create a backup script `backup.sh`:

```bash
#!/bin/bash
BACKUP_DIR="./backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="$BACKUP_DIR/iesa_backup_$TIMESTAMP.sql.gz"

mkdir -p $BACKUP_DIR

docker exec iesa-postgres pg_dump -U postgres iesa_dev | gzip > $BACKUP_FILE

echo "Backup completed: $BACKUP_FILE"

# Keep only last 7 backups
ls -t $BACKUP_DIR/iesa_backup_*.sql.gz | tail -n +8 | xargs -r rm
```

Make it executable:
```bash
chmod +x backup.sh
./backup.sh
```

## Troubleshooting

### Port Already in Use

If port 5432 is already in use:

1. Change port in `.env`:
   ```bash
   DB_PORT=5433
   ```

2. Update `docker-compose.yml`:
   ```yaml
   ports:
     - "5433:5432"
   ```

3. Update application configuration to use port 5433

### Container Won't Start

```bash
# Check logs
docker-compose logs postgres

# Remove and recreate
docker-compose down -v
docker-compose up -d
```

### Database Connection Refused

1. Ensure container is running:
   ```bash
   docker-compose ps
   ```

2. Check container logs:
   ```bash
   docker-compose logs postgres
   ```

3. Verify connection from inside container:
   ```bash
   docker exec -it iesa-postgres psql -U postgres -d iesa_dev
   ```

4. Check network connectivity:
   ```bash
   docker network inspect iesa-server_iesa-network
   ```

### Reset Everything

```bash
# Stop and remove containers, networks, volumes
docker-compose down -v

# Remove all IESA-related volumes
docker volume prune

# Start fresh
docker-compose up -d
```

### View Database Size

```bash
docker exec iesa-postgres psql -U postgres -d iesa_dev -c "
SELECT
    pg_size_pretty(pg_database_size('iesa_dev')) as database_size;
"
```

### View Table Sizes

```bash
docker exec iesa-postgres psql -U postgres -d iesa_dev -c "
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
"
```

## Production Considerations

For production deployment:

1. **Change default passwords** in `.env`
2. **Use secrets management** (Docker secrets, Vault)
3. **Enable SSL/TLS** for database connections
4. **Set up automated backups**
5. **Configure resource limits**:

```yaml
services:
  postgres:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

6. **Use persistent volumes** with proper backup strategy
7. **Monitor container health** and set up alerts
8. **Use Docker secrets** for sensitive data:

```yaml
services:
  postgres:
    secrets:
      - db_password
    environment:
      POSTGRES_PASSWORD_FILE: /run/secrets/db_password

secrets:
  db_password:
    file: ./secrets/db_password.txt
```

## Docker Compose Commands Reference

| Command | Description |
|---------|-------------|
| `docker-compose up -d` | Start services in background |
| `docker-compose down` | Stop and remove containers |
| `docker-compose down -v` | Stop and remove containers and volumes |
| `docker-compose ps` | List running containers |
| `docker-compose logs -f` | Follow logs |
| `docker-compose restart` | Restart services |
| `docker-compose stop` | Stop services |
| `docker-compose start` | Start stopped services |
| `docker-compose exec postgres bash` | Execute bash in container |
| `docker-compose build` | Build/rebuild services |
| `docker-compose pull` | Pull latest images |

## Environment Variables

All configuration is managed through `.env` file:

```bash
# Database
DB_NAME=iesa_dev
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_PORT=5432
DB_HOST=localhost

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# pgAdmin
PGADMIN_EMAIL=admin@iesa.com
PGADMIN_PASSWORD=admin123
PGADMIN_PORT=5050
```

## Health Checks

Services include health checks:

```bash
# Check service health
docker-compose ps

# Manual health check
docker exec iesa-postgres pg_isready -U postgres -d iesa_dev
docker exec iesa-redis redis-cli ping
```

## Next Steps

1. ✅ PostgreSQL database running in Docker
2. ✅ Redis cache running in Docker
3. ✅ pgAdmin available for database management
4. Run Spring Boot application: `mvn spring-boot:run`
5. Access pgAdmin: http://localhost:5050
6. Set up automated backups
7. Configure monitoring and alerts

## Support

For issues or questions:
- Check logs: `docker-compose logs`
- Review Docker documentation: https://docs.docker.com
- PostgreSQL documentation: https://www.postgresql.org/docs/

---

**Note:** Remember to add `.env` to `.gitignore` to prevent committing sensitive credentials.
