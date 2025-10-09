#!/bin/bash

# IESA Database Startup Script
# This script starts PostgreSQL and Redis using Docker Compose

set -e

echo "========================================="
echo "Starting IESA Database Services"
echo "========================================="
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running!"
    echo "Please start Docker Desktop and try again."
    exit 1
fi

# Check if .env file exists
if [ ! -f .env ]; then
    echo "⚠️  .env file not found. Creating from .env.example..."
    cp .env.example .env
    echo "✅ Created .env file. Please review and update if needed."
    echo ""
fi

# Start Docker Compose services
echo "🚀 Starting Docker services..."
echo ""

# Check if user wants pgAdmin
read -p "Do you want to start pgAdmin (Database UI)? [y/N] " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "Starting PostgreSQL, Redis, and pgAdmin..."
    docker-compose --profile tools up -d
    echo ""
    echo "✅ Services started successfully!"
    echo ""
    echo "📊 pgAdmin is available at: http://localhost:5050"
    echo "   Email: admin@iesa.com"
    echo "   Password: admin123"
else
    echo "Starting PostgreSQL and Redis..."
    docker-compose up -d
    echo ""
    echo "✅ Services started successfully!"
fi

echo ""
echo "Service Status:"
echo "========================================="
docker-compose ps
echo ""

# Wait for PostgreSQL to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
sleep 5

# Test PostgreSQL connection
if docker exec iesa-postgres pg_isready -U postgres -d iesa_dev > /dev/null 2>&1; then
    echo "✅ PostgreSQL is ready!"
else
    echo "⚠️  PostgreSQL is starting up. Please wait a moment..."
fi

echo ""
echo "📝 Quick commands:"
echo "   View logs:        docker-compose logs -f"
echo "   Stop services:    docker-compose stop"
echo "   Restart services: docker-compose restart"
echo "   Access DB shell:  docker exec -it iesa-postgres psql -U postgres -d iesa_dev"
echo ""
echo "🚀 You can now run the Spring Boot application:"
echo "   mvn spring-boot:run -Dspring-boot.run.profiles=dev"
echo ""
echo "========================================="
echo "Database services are running! 🎉"
echo "========================================="
