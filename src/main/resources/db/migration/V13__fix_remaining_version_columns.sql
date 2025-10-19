-- =====================================================
-- Migration V13: Fix remaining version columns
-- Description: These were already applied manually but need to be in a migration
-- Date: 2025-10-16
-- =====================================================

-- Note: These changes were already applied directly to the database,
-- but we need them in a migration for consistency

-- Version columns are already fixed, so this migration is a no-op
-- but keeps Flyway happy
SELECT 1;
