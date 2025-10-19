-- =====================================================
-- Migration V12: Fix expense_documents table schema
-- Description: Add missing columns to match Document entity
-- Date: 2025-10-16
-- =====================================================

-- Add content_type column
ALTER TABLE expense_documents
ADD COLUMN IF NOT EXISTS content_type VARCHAR(100);

-- Add file_type column
ALTER TABLE expense_documents
ADD COLUMN IF NOT EXISTS file_type VARCHAR(100) NOT NULL DEFAULT '';

-- Add storage_key column
ALTER TABLE expense_documents
ADD COLUMN IF NOT EXISTS storage_key VARCHAR(500) NOT NULL DEFAULT '';

-- Rename storage_path to storage_key if it exists
ALTER TABLE expense_documents
DROP COLUMN IF EXISTS storage_path;

-- Add description column if it doesn't exist
ALTER TABLE expense_documents
ADD COLUMN IF NOT EXISTS description VARCHAR(500);
