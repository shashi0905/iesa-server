-- =====================================================
-- Migration V9: Insert Sample Approval Workflows
-- Description: Inserts sample workflow data for testing
-- Date: 2025-10-15
-- =====================================================

-- Insert sample approval workflows

-- Workflow 1: Simple Manager Approval (for small expenses)
INSERT INTO approval_workflows (id, name, description, is_active, trigger_conditions)
VALUES (
    '850e8400-e29b-41d4-a716-446655440001',
    'Simple Manager Approval',
    'Single-step manager approval for expenses under $1000',
    true,
    '{"maxAmount": 1000, "currency": "USD"}'::jsonb
);

-- Workflow 2: Two-Level Approval (for medium expenses)
INSERT INTO approval_workflows (id, name, description, is_active, trigger_conditions)
VALUES (
    '850e8400-e29b-41d4-a716-446655440002',
    'Two-Level Approval',
    'Manager and Finance approval for expenses between $1000 and $5000',
    true,
    '{"minAmount": 1000, "maxAmount": 5000, "currency": "USD"}'::jsonb
);

-- Workflow 3: Executive Approval (for large expenses)
INSERT INTO approval_workflows (id, name, description, is_active, trigger_conditions)
VALUES (
    '850e8400-e29b-41d4-a716-446655440003',
    'Executive Approval',
    'Multi-level approval for expenses exceeding $5000',
    true,
    '{"minAmount": 5000, "currency": "USD"}'::jsonb
);

-- Insert approval steps for Workflow 1 (Simple Manager Approval)
WITH
    workflow1 AS (SELECT id FROM approval_workflows WHERE name = 'Simple Manager Approval'),
    manager_role AS (SELECT id FROM roles WHERE role_type = 'MANAGER')
INSERT INTO approval_steps (id, workflow_id, step_order, approver_role_id, is_mandatory, step_name)
SELECT
    gen_random_uuid(),
    workflow1.id,
    1,
    manager_role.id,
    true,
    'Manager Review'
FROM workflow1, manager_role;

-- Insert approval steps for Workflow 2 (Two-Level Approval)
-- Step 1: Manager approval
WITH
    workflow2 AS (SELECT id FROM approval_workflows WHERE name = 'Two-Level Approval'),
    manager_role AS (SELECT id FROM roles WHERE role_type = 'MANAGER')
INSERT INTO approval_steps (id, workflow_id, step_order, approver_role_id, is_mandatory, step_name)
SELECT
    gen_random_uuid(),
    workflow2.id,
    1,
    manager_role.id,
    true,
    'Manager Review'
FROM workflow2, manager_role;

-- Step 2: Finance approval
WITH
    workflow2 AS (SELECT id FROM approval_workflows WHERE name = 'Two-Level Approval'),
    finance_role AS (SELECT id FROM roles WHERE role_type = 'FINANCE_ADMIN')
INSERT INTO approval_steps (id, workflow_id, step_order, approver_role_id, is_mandatory, step_name)
SELECT
    gen_random_uuid(),
    workflow2.id,
    2,
    finance_role.id,
    true,
    'Finance Approval'
FROM workflow2, finance_role;

-- Insert approval steps for Workflow 3 (Executive Approval)
-- Step 1: Manager approval
WITH
    workflow3 AS (SELECT id FROM approval_workflows WHERE name = 'Executive Approval'),
    manager_role AS (SELECT id FROM roles WHERE role_type = 'MANAGER')
INSERT INTO approval_steps (id, workflow_id, step_order, approver_role_id, is_mandatory, step_name)
SELECT
    gen_random_uuid(),
    workflow3.id,
    1,
    manager_role.id,
    true,
    'Manager Review'
FROM workflow3, manager_role;

-- Step 2: Finance approval
WITH
    workflow3 AS (SELECT id FROM approval_workflows WHERE name = 'Executive Approval'),
    finance_role AS (SELECT id FROM roles WHERE role_type = 'FINANCE_ADMIN')
INSERT INTO approval_steps (id, workflow_id, step_order, approver_role_id, is_mandatory, step_name)
SELECT
    gen_random_uuid(),
    workflow3.id,
    2,
    finance_role.id,
    true,
    'Finance Review'
FROM workflow3, finance_role;

-- Step 3: Admin final approval
WITH
    workflow3 AS (SELECT id FROM approval_workflows WHERE name = 'Executive Approval'),
    admin_user AS (SELECT id FROM users WHERE username = 'admin')
INSERT INTO approval_steps (id, workflow_id, step_order, approver_user_id, is_mandatory, step_name)
SELECT
    gen_random_uuid(),
    workflow3.id,
    3,
    admin_user.id,
    true,
    'Executive Approval'
FROM workflow3, admin_user;

-- Insert sample workflow history for existing expenses
-- History for Expense 1 (Alice's laptop - APPROVED)
WITH
    expense1 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440001'),
    alice AS (SELECT id FROM users WHERE username = 'alice.dev'),
    john_manager AS (SELECT id FROM users WHERE username = 'john.manager')
INSERT INTO workflow_history (id, expense_id, from_status, to_status, actor_id, comment, timestamp)
SELECT
    gen_random_uuid(),
    expense1.id,
    'DRAFT',
    'SUBMITTED',
    alice.id,
    'Submitted for approval',
    TIMESTAMP '2025-10-02 09:00:00'
FROM expense1, alice
UNION ALL
SELECT
    gen_random_uuid(),
    expense1.id,
    'SUBMITTED',
    'APPROVED',
    john_manager.id,
    'Approved - necessary for development work',
    TIMESTAMP '2025-10-03 10:30:00'
FROM expense1, john_manager;

-- History for Expense 2 (Bob's conference - SUBMITTED)
WITH
    expense2 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440002'),
    bob AS (SELECT id FROM users WHERE username = 'bob.dev')
INSERT INTO workflow_history (id, expense_id, from_status, to_status, actor_id, comment, timestamp)
SELECT
    gen_random_uuid(),
    expense2.id,
    'DRAFT',
    'SUBMITTED',
    bob.id,
    'Submitted for approval',
    TIMESTAMP '2025-10-06 14:20:00'
FROM expense2, bob;

-- History for Expense 3 (Jane's marketing - APPROVED)
WITH
    expense3 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440003'),
    jane AS (SELECT id FROM users WHERE username = 'jane.marketing'),
    admin_user AS (SELECT id FROM users WHERE username = 'admin')
INSERT INTO workflow_history (id, expense_id, from_status, to_status, actor_id, comment, timestamp)
SELECT
    gen_random_uuid(),
    expense3.id,
    'DRAFT',
    'SUBMITTED',
    jane.id,
    'Submitted for approval',
    TIMESTAMP '2025-09-29 11:00:00'
FROM expense3, jane
UNION ALL
SELECT
    gen_random_uuid(),
    expense3.id,
    'SUBMITTED',
    'APPROVED',
    admin_user.id,
    'Approved for Q1 campaign',
    TIMESTAMP '2025-09-30 09:15:00'
FROM expense3, admin_user;

-- History for Expense 5 (John's lunch - REJECTED)
WITH
    expense5 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440005'),
    john AS (SELECT id FROM users WHERE username = 'john.manager'),
    admin_user AS (SELECT id FROM users WHERE username = 'admin')
INSERT INTO workflow_history (id, expense_id, from_status, to_status, actor_id, comment, timestamp)
SELECT
    gen_random_uuid(),
    expense5.id,
    'DRAFT',
    'SUBMITTED',
    john.id,
    'Submitted for approval',
    TIMESTAMP '2025-10-09 13:00:00'
FROM expense5, john
UNION ALL
SELECT
    gen_random_uuid(),
    expense5.id,
    'SUBMITTED',
    'REJECTED',
    admin_user.id,
    'Please allocate to department budget instead of project budget',
    TIMESTAMP '2025-10-09 15:30:00'
FROM expense5, admin_user;

-- Insert sample comments
-- Comments for Expense 2 (Bob's conference - pending)
WITH
    expense2 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440002'),
    john_manager AS (SELECT id FROM users WHERE username = 'john.manager'),
    bob AS (SELECT id FROM users WHERE username = 'bob.dev')
INSERT INTO comments (id, expense_id, author_id, content, is_internal, created_at)
SELECT
    gen_random_uuid(),
    expense2.id,
    john_manager.id,
    'Can you provide more details about the conference agenda?',
    false,
    TIMESTAMP '2025-10-07 09:00:00'
FROM expense2, john_manager
UNION ALL
SELECT
    gen_random_uuid(),
    expense2.id,
    bob.id,
    'The conference covers advanced React patterns, state management, and performance optimization. It will help with the IESA frontend development.',
    false,
    TIMESTAMP '2025-10-07 10:30:00'
FROM expense2, bob;

-- Internal comment from manager
WITH
    expense2 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440002'),
    john_manager AS (SELECT id FROM users WHERE username = 'john.manager')
INSERT INTO comments (id, expense_id, author_id, content, is_internal, created_at)
SELECT
    gen_random_uuid(),
    expense2.id,
    john_manager.id,
    'Looks good, will approve after checking budget allocation',
    true,
    TIMESTAMP '2025-10-07 11:00:00'
FROM expense2, john_manager;

-- Insert sample approval actions
-- Approval action for Expense 1
WITH
    expense1 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440001'),
    john_manager AS (SELECT id FROM users WHERE username = 'john.manager'),
    manager_step AS (
        SELECT s.id FROM approval_steps s
        JOIN approval_workflows w ON s.workflow_id = w.id
        WHERE w.name = 'Simple Manager Approval' AND s.step_order = 1
    )
INSERT INTO approval_actions (id, expense_id, step_id, approver_id, action, comment, action_date)
SELECT
    gen_random_uuid(),
    expense1.id,
    manager_step.id,
    john_manager.id,
    'APPROVED',
    'Approved - necessary for development work',
    TIMESTAMP '2025-10-03 10:30:00'
FROM expense1, john_manager, manager_step;

-- Approval action for Expense 3
WITH
    expense3 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440003'),
    admin_user AS (SELECT id FROM users WHERE username = 'admin'),
    finance_step AS (
        SELECT s.id FROM approval_steps s
        JOIN approval_workflows w ON s.workflow_id = w.id
        WHERE w.name = 'Two-Level Approval' AND s.step_order = 2
    )
INSERT INTO approval_actions (id, expense_id, step_id, approver_id, action, comment, action_date)
SELECT
    gen_random_uuid(),
    expense3.id,
    finance_step.id,
    admin_user.id,
    'APPROVED',
    'Approved for Q1 campaign',
    TIMESTAMP '2025-09-30 09:15:00'
FROM expense3, admin_user, finance_step;

-- Rejection action for Expense 5
WITH
    expense5 AS (SELECT id FROM expenses WHERE id = '750e8400-e29b-41d4-a716-446655440005'),
    admin_user AS (SELECT id FROM users WHERE username = 'admin'),
    finance_step AS (
        SELECT s.id FROM approval_steps s
        JOIN approval_workflows w ON s.workflow_id = w.id
        WHERE w.name = 'Simple Manager Approval' AND s.step_order = 1
    )
INSERT INTO approval_actions (id, expense_id, step_id, approver_id, action, comment, action_date)
SELECT
    gen_random_uuid(),
    expense5.id,
    finance_step.id,
    admin_user.id,
    'REJECTED',
    'Please allocate to department budget instead of project budget',
    TIMESTAMP '2025-10-09 15:30:00'
FROM expense5, admin_user, finance_step;
