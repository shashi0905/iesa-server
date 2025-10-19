-- =====================================================
-- Migration V11: Fix version column type
-- Description: Change version column from INTEGER to BIGINT in all affected tables
-- Date: 2025-10-16
-- =====================================================

-- Fix version column type in segments table
ALTER TABLE segments
ALTER COLUMN version TYPE BIGINT;

-- Fix version column type in department_segment_mappings table
ALTER TABLE department_segment_mappings
ALTER COLUMN version TYPE BIGINT;

-- Fix version column type in expenses table
ALTER TABLE expenses
ALTER COLUMN version TYPE BIGINT;

-- Fix version column type in segment_allocations table
ALTER TABLE segment_allocations
ALTER COLUMN version TYPE BIGINT;

-- Fix version column type in expense_documents table
ALTER TABLE expense_documents
ALTER COLUMN version TYPE BIGINT;
