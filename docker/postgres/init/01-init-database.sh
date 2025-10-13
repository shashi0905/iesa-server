#!/bin/bash
set -e

# This script runs when the PostgreSQL container is first created
# It creates the database and sets up initial configuration

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Create extensions
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    CREATE EXTENSION IF NOT EXISTS "pg_trgm";

    -- Grant privileges
    GRANT ALL PRIVILEGES ON DATABASE $POSTGRES_DB TO $POSTGRES_USER;

    -- Set timezone
    SET timezone = 'UTC';

    -- Log initialization
    SELECT 'Database initialization completed for: $POSTGRES_DB' AS status;
EOSQL

echo "PostgreSQL database '$POSTGRES_DB' initialized successfully!"
