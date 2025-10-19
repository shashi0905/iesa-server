-- =====================================================
-- Migration V14: Add payment columns to expenses table
-- Description: Adds payment_date and payment_reference columns
-- Date: 2025-10-18
-- =====================================================

-- Add payment_date column
ALTER TABLE expenses ADD COLUMN IF NOT EXISTS payment_date DATE;

-- Add payment_reference column
ALTER TABLE expenses ADD COLUMN IF NOT EXISTS payment_reference VARCHAR(100);

-- Add index for payment_date
CREATE INDEX IF NOT EXISTS idx_expense_payment_date ON expenses(payment_date);

-- Add comment
COMMENT ON COLUMN expenses.payment_date IS 'Date when the expense was paid';
COMMENT ON COLUMN expenses.payment_reference IS 'Payment reference number or transaction ID';
