#!/bin/bash

# IESA Database Stop Script
# This script stops PostgreSQL and Redis Docker containers

set -e

echo "========================================="
echo "Stopping IESA Database Services"
echo "========================================="
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running!"
    exit 1
fi

# Ask if user wants to remove volumes
echo "Choose stop option:"
echo "1) Stop services (keep data)"
echo "2) Stop and remove services (keep data)"
echo "3) Stop and remove everything including data (DESTRUCTIVE)"
echo ""
read -p "Enter your choice [1-3]: " -n 1 -r
echo ""

case $REPLY in
    1)
        echo "🛑 Stopping services..."
        docker-compose stop
        echo "✅ Services stopped. Data preserved."
        ;;
    2)
        echo "🛑 Stopping and removing containers..."
        docker-compose down
        echo "✅ Services stopped and removed. Data volumes preserved."
        ;;
    3)
        read -p "⚠️  This will DELETE ALL DATA! Are you sure? [y/N] " -n 1 -r
        echo ""
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            echo "🗑️  Stopping and removing containers and volumes..."
            docker-compose down -v
            echo "✅ Services and data removed completely."
        else
            echo "❌ Cancelled. No changes made."
        fi
        ;;
    *)
        echo "❌ Invalid choice. No changes made."
        exit 1
        ;;
esac

echo ""
echo "Current status:"
docker-compose ps

echo ""
echo "========================================="
echo "Done! 🎉"
echo "========================================="
