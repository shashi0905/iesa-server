-- =====================================================
-- Migration V6: Create Expense Management Tables
-- Description: Creates tables for expense management module
-- Date: 2025-10-14
-- =====================================================

-- Create expenses table
CREATE TABLE expenses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submitter_id UUID NOT NULL,
    expense_date DATE NOT NULL,
    vendor VARCHAR(200),
    total_amount DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    description VARCHAR(2000),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    submission_date DATE,
    approval_date DATE,
    rejection_reason TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_expense_submitter FOREIGN KEY (submitter_id)
        REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_expense_amount_positive CHECK (total_amount > 0)
);

-- Create segment_allocations table
CREATE TABLE segment_allocations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    expense_id UUID NOT NULL,
    segment_id UUID NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    percentage DECIMAL(5, 2) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_allocation_expense FOREIGN KEY (expense_id)
        REFERENCES expenses(id) ON DELETE CASCADE,
    CONSTRAINT fk_allocation_segment FOREIGN KEY (segment_id)
        REFERENCES segments(id) ON DELETE RESTRICT,
    CONSTRAINT chk_allocation_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_allocation_percentage_range CHECK (percentage > 0 AND percentage <= 100)
);

-- Create expense_documents table
CREATE TABLE expense_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    expense_id UUID NOT NULL,
    uploaded_by_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    upload_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_document_expense FOREIGN KEY (expense_id)
        REFERENCES expenses(id) ON DELETE CASCADE,
    CONSTRAINT fk_document_uploaded_by FOREIGN KEY (uploaded_by_id)
        REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_document_file_size_positive CHECK (file_size > 0)
);

-- Create indexes for expenses table
CREATE INDEX idx_expense_submitter ON expenses(submitter_id);
CREATE INDEX idx_expense_date ON expenses(expense_date);
CREATE INDEX idx_expense_status ON expenses(status);
CREATE INDEX idx_expense_submission_date ON expenses(submission_date);
CREATE INDEX idx_expense_approval_date ON expenses(approval_date);
CREATE INDEX idx_expense_deleted ON expenses(deleted_at);

-- Create indexes for segment_allocations table
CREATE INDEX idx_allocation_expense ON segment_allocations(expense_id);
CREATE INDEX idx_allocation_segment ON segment_allocations(segment_id);

-- Create indexes for expense_documents table
CREATE INDEX idx_document_expense ON expense_documents(expense_id);
CREATE INDEX idx_document_uploaded_by ON expense_documents(uploaded_by_id);
CREATE INDEX idx_document_upload_date ON expense_documents(upload_date);

-- Add comments for documentation
COMMENT ON TABLE expenses IS 'Stores expense records with multi-segment allocation support';
COMMENT ON TABLE segment_allocations IS 'Stores percentage-based allocation of expenses to segments';
COMMENT ON TABLE expense_documents IS 'Stores metadata for expense-related documents (receipts, invoices)';

COMMENT ON COLUMN expenses.status IS 'Expense status: DRAFT, SUBMITTED, APPROVED, REJECTED, PAID';
COMMENT ON COLUMN expenses.currency IS 'ISO 4217 currency code (e.g., USD, EUR, GBP)';
COMMENT ON COLUMN segment_allocations.percentage IS 'Percentage of total expense allocated to this segment (0-100)';
COMMENT ON COLUMN segment_allocations.amount IS 'Calculated amount = total_amount * percentage / 100';
COMMENT ON COLUMN expense_documents.storage_path IS 'File system or cloud storage path for the document';
