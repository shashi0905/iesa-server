-- =====================================================
-- Migration V7: Insert Sample Expenses
-- Description: Inserts sample expense data for testing
-- Date: 2025-10-14
-- =====================================================

-- Insert sample expenses

-- Expense 1: Alice's laptop purchase (APPROVED)
INSERT INTO expenses (id, submitter_id, expense_date, vendor, total_amount, currency, description, status, submission_date, approval_date)
VALUES (
    '750e8400-e29b-41d4-a716-446655440001',
    '650e8400-e29b-41d4-a716-446655440004', -- alice.dev
    '2025-10-01',
    'Apple Store',
    2499.00,
    'USD',
    'MacBook Pro 16" for development work',
    'APPROVED',
    '2025-10-02',
    '2025-10-03'
);

-- Expense 2: Bob's conference travel (SUBMITTED, pending approval)
INSERT INTO expenses (id, submitter_id, expense_date, vendor, total_amount, currency, description, status, submission_date)
VALUES (
    '750e8400-e29b-41d4-a716-446655440002',
    '650e8400-e29b-41d4-a716-446655440005', -- bob.dev
    '2025-10-05',
    'Delta Airlines',
    856.50,
    'USD',
    'Flight tickets for React Conference 2025',
    'SUBMITTED',
    '2025-10-06'
);

-- Expense 3: Jane's marketing campaign materials (APPROVED)
INSERT INTO expenses (id, submitter_id, expense_date, vendor, total_amount, currency, description, status, submission_date, approval_date)
VALUES (
    '750e8400-e29b-41d4-a716-446655440003',
    '650e8400-e29b-41d4-a716-446655440003', -- jane.marketing
    '2025-09-28',
    'Printify',
    1250.00,
    'USD',
    'Promotional materials for Q1 campaign',
    'APPROVED',
    '2025-09-29',
    '2025-09-30'
);

-- Expense 4: Alice's software subscription (DRAFT)
INSERT INTO expenses (id, submitter_id, expense_date, vendor, total_amount, currency, description, status)
VALUES (
    '750e8400-e29b-41d4-a716-446655440004',
    '650e8400-e29b-41d4-a716-446655440004', -- alice.dev
    '2025-10-10',
    'JetBrains',
    199.00,
    'USD',
    'IntelliJ IDEA Ultimate annual subscription',
    'DRAFT'
);

-- Expense 5: John's team lunch (REJECTED)
INSERT INTO expenses (id, submitter_id, expense_date, vendor, total_amount, currency, description, status, submission_date, rejection_reason)
VALUES (
    '750e8400-e29b-41d4-a716-446655440005',
    '650e8400-e29b-41d4-a716-446655440002', -- john.manager
    '2025-10-08',
    'Local Restaurant',
    385.00,
    'USD',
    'Team lunch for project milestone celebration',
    'REJECTED',
    '2025-10-09',
    'Please allocate to department budget instead of project budget'
);

-- Expense 6: Bob's training course (APPROVED)
INSERT INTO expenses (id, submitter_id, expense_date, vendor, total_amount, currency, description, status, submission_date, approval_date)
VALUES (
    '750e8400-e29b-41d4-a716-446655440006',
    '650e8400-e29b-41d4-a716-446655440005', -- bob.dev
    '2025-09-15',
    'Udemy',
    349.99,
    'USD',
    'Advanced React & TypeScript course',
    'APPROVED',
    '2025-09-16',
    '2025-09-17'
);

-- Insert segment allocations for expenses

-- Allocations for Expense 1 (Alice's laptop): 100% to Engineering Cost Center
WITH expense1 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440001'),
     segment_eng AS (SELECT id FROM segments WHERE code = 'CC-ENG')
INSERT INTO segment_allocations (id, expense_id, segment_id, amount, percentage, description)
SELECT
    gen_random_uuid(),
    expense1.id,
    segment_eng.id,
    2499.00,
    100.00,
    'Engineering equipment'
FROM expense1, segment_eng;

-- Allocations for Expense 2 (Bob's travel): 60% IESA Project, 40% Training Category
WITH expense2 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440002'),
     segment_iesa AS (SELECT id FROM segments WHERE code = 'PRJ-IESA'),
     segment_training AS (SELECT id FROM segments WHERE code = 'CAT-TRAINING')
INSERT INTO segment_allocations (id, expense_id, segment_id, amount, percentage, description)
SELECT
    gen_random_uuid(),
    expense2.id,
    segment_iesa.id,
    513.90,
    60.00,
    'Conference attendance for IESA project knowledge'
FROM expense2, segment_iesa
UNION ALL
SELECT
    gen_random_uuid(),
    expense2.id,
    segment_training.id,
    342.60,
    40.00,
    'Professional development and training'
FROM expense2, segment_training;

-- Allocations for Expense 3 (Jane's marketing): 100% Q1 Marketing Campaign
WITH expense3 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440003'),
     segment_mkt AS (SELECT id FROM segments WHERE code = 'PRJ-MKT-Q1')
INSERT INTO segment_allocations (id, expense_id, segment_id, amount, percentage, description)
SELECT
    gen_random_uuid(),
    expense3.id,
    segment_mkt.id,
    1250.00,
    100.00,
    'Campaign materials'
FROM expense3, segment_mkt;

-- Allocations for Expense 4 (Alice's software - DRAFT): 100% Engineering Cost Center
WITH expense4 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440004'),
     segment_eng AS (SELECT id FROM segments WHERE code = 'CC-ENG')
INSERT INTO segment_allocations (id, expense_id, segment_id, amount, percentage, description)
SELECT
    gen_random_uuid(),
    expense4.id,
    segment_eng.id,
    199.00,
    100.00,
    'Development tools'
FROM expense4, segment_eng;

-- Allocations for Expense 5 (John's lunch - REJECTED): 100% IESA Project
WITH expense5 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440005'),
     segment_iesa AS (SELECT id FROM segments WHERE code = 'PRJ-IESA')
INSERT INTO segment_allocations (id, expense_id, segment_id, amount, percentage, description)
SELECT
    gen_random_uuid(),
    expense5.id,
    segment_iesa.id,
    385.00,
    100.00,
    'Team building'
FROM expense5, segment_iesa;

-- Allocations for Expense 6 (Bob's training): 50% IESA Backend, 50% Training Category
WITH expense6 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440006'),
     segment_iesa_be AS (SELECT id FROM segments WHERE code = 'PRJ-IESA-BE'),
     segment_training AS (SELECT id FROM segments WHERE code = 'CAT-TRAINING')
INSERT INTO segment_allocations (id, expense_id, segment_id, amount, percentage, description)
SELECT
    gen_random_uuid(),
    expense6.id,
    segment_iesa_be.id,
    175.00,
    50.00,
    'Backend development skills improvement'
FROM expense6, segment_iesa_be
UNION ALL
SELECT
    gen_random_uuid(),
    expense6.id,
    segment_training.id,
    174.99,
    50.00,
    'Professional development'
FROM expense6, segment_training;

-- Insert sample documents for some expenses

-- Document for Expense 1 (Alice's laptop receipt)
WITH expense1 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440001'),
     alice AS (SELECT id FROM users WHERE username = 'alice.dev')
INSERT INTO expense_documents (id, expense_id, uploaded_by_id, file_name, original_file_name, file_size, mime_type, storage_path, upload_date)
SELECT
    gen_random_uuid(),
    expense1.id,
    alice.id,
    'receipt_750e8400_e29b_41d4_a716_446655440001_1.pdf',
    'apple_store_receipt_2499.pdf',
    245678,
    'application/pdf',
    '/documents/expenses/2025/10/receipt_750e8400_e29b_41d4_a716_446655440001_1.pdf',
    '2025-10-02 09:30:00'
FROM expense1, alice;

-- Document for Expense 3 (Jane's marketing materials invoice)
WITH expense3 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440003'),
     jane AS (SELECT id FROM users WHERE username = 'jane.marketing')
INSERT INTO expense_documents (id, expense_id, uploaded_by_id, file_name, original_file_name, file_size, mime_type, storage_path, upload_date)
SELECT
    gen_random_uuid(),
    expense3.id,
    jane.id,
    'invoice_750e8400_e29b_41d4_a716_446655440003_1.pdf',
    'printify_invoice_1250.pdf',
    189234,
    'application/pdf',
    '/documents/expenses/2025/09/invoice_750e8400_e29b_41d4_a716_446655440003_1.pdf',
    '2025-09-29 14:20:00'
FROM expense3, jane;

-- Document for Expense 6 (Bob's training course receipt)
WITH expense6 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440006'),
     bob AS (SELECT id FROM users WHERE username = 'bob.dev')
INSERT INTO expense_documents (id, expense_id, uploaded_by_id, file_name, original_file_name, file_size, mime_type, storage_path, upload_date)
SELECT
    gen_random_uuid(),
    expense6.id,
    bob.id,
    'receipt_750e8400_e29b_41d4_a716_446655440006_1.pdf',
    'udemy_receipt_349.99.pdf',
    123456,
    'application/pdf',
    '/documents/expenses/2025/09/receipt_750e8400_e29b_41d4_a716_446655440006_1.pdf',
    '2025-09-16 16:45:00'
FROM expense6, bob;
