-- Insert default permissions
INSERT INTO permissions (id, permission_type, description, resource, action) VALUES
-- User Management
(gen_random_uuid(), 'USER_READ', 'Read user information', 'USER', 'READ'),
(gen_random_uuid(), 'USER_CREATE', 'Create new users', 'USER', 'CREATE'),
(gen_random_uuid(), 'USER_UPDATE', 'Update user information', 'USER', 'UPDATE'),
(gen_random_uuid(), 'USER_DELETE', 'Delete users', 'USER', 'DELETE'),

-- Expense Management
(gen_random_uuid(), 'EXPENSE_READ_OWN', 'Read own expenses', 'EXPENSE', 'READ_OWN'),
(gen_random_uuid(), 'EXPENSE_READ_DEPARTMENT', 'Read department expenses', 'EXPENSE', 'READ_DEPARTMENT'),
(gen_random_uuid(), 'EXPENSE_READ_ALL', 'Read all expenses', 'EXPENSE', 'READ_ALL'),
(gen_random_uuid(), 'EXPENSE_CREATE', 'Create expenses', 'EXPENSE', 'CREATE'),
(gen_random_uuid(), 'EXPENSE_UPDATE_OWN', 'Update own expenses', 'EXPENSE', 'UPDATE_OWN'),
(gen_random_uuid(), 'EXPENSE_UPDATE_ALL', 'Update all expenses', 'EXPENSE', 'UPDATE_ALL'),
(gen_random_uuid(), 'EXPENSE_DELETE_OWN', 'Delete own expenses', 'EXPENSE', 'DELETE_OWN'),
(gen_random_uuid(), 'EXPENSE_DELETE_ALL', 'Delete all expenses', 'EXPENSE', 'DELETE_ALL'),

-- Approval
(gen_random_uuid(), 'EXPENSE_APPROVE_DEPARTMENT', 'Approve department expenses', 'EXPENSE', 'APPROVE_DEPARTMENT'),
(gen_random_uuid(), 'EXPENSE_APPROVE_ALL', 'Approve all expenses', 'EXPENSE', 'APPROVE_ALL'),
(gen_random_uuid(), 'EXPENSE_REJECT', 'Reject expenses', 'EXPENSE', 'REJECT'),

-- Segment Management
(gen_random_uuid(), 'SEGMENT_READ', 'Read segments', 'SEGMENT', 'READ'),
(gen_random_uuid(), 'SEGMENT_CREATE', 'Create segments', 'SEGMENT', 'CREATE'),
(gen_random_uuid(), 'SEGMENT_UPDATE', 'Update segments', 'SEGMENT', 'UPDATE'),
(gen_random_uuid(), 'SEGMENT_DELETE', 'Delete segments', 'SEGMENT', 'DELETE'),

-- Budget Management
(gen_random_uuid(), 'BUDGET_READ', 'Read budgets', 'BUDGET', 'READ'),
(gen_random_uuid(), 'BUDGET_CREATE', 'Create budgets', 'BUDGET', 'CREATE'),
(gen_random_uuid(), 'BUDGET_UPDATE', 'Update budgets', 'BUDGET', 'UPDATE'),
(gen_random_uuid(), 'BUDGET_DELETE', 'Delete budgets', 'BUDGET', 'DELETE'),

-- Reporting
(gen_random_uuid(), 'REPORT_READ_OWN', 'Read own reports', 'REPORT', 'READ_OWN'),
(gen_random_uuid(), 'REPORT_READ_DEPARTMENT', 'Read department reports', 'REPORT', 'READ_DEPARTMENT'),
(gen_random_uuid(), 'REPORT_READ_ALL', 'Read all reports', 'REPORT', 'READ_ALL'),
(gen_random_uuid(), 'REPORT_CREATE', 'Create reports', 'REPORT', 'CREATE'),
(gen_random_uuid(), 'REPORT_EXPORT', 'Export reports', 'REPORT', 'EXPORT'),

-- Department Management
(gen_random_uuid(), 'DEPARTMENT_READ', 'Read departments', 'DEPARTMENT', 'READ'),
(gen_random_uuid(), 'DEPARTMENT_CREATE', 'Create departments', 'DEPARTMENT', 'CREATE'),
(gen_random_uuid(), 'DEPARTMENT_UPDATE', 'Update departments', 'DEPARTMENT', 'UPDATE'),
(gen_random_uuid(), 'DEPARTMENT_DELETE', 'Delete departments', 'DEPARTMENT', 'DELETE'),

-- System Configuration
(gen_random_uuid(), 'SYSTEM_CONFIG_READ', 'Read system configuration', 'SYSTEM', 'CONFIG_READ'),
(gen_random_uuid(), 'SYSTEM_CONFIG_UPDATE', 'Update system configuration', 'SYSTEM', 'CONFIG_UPDATE');

-- Insert default roles
INSERT INTO roles (id, name, role_type, description) VALUES
(gen_random_uuid(), 'Employee', 'EMPLOYEE', 'Standard employee role - can submit expenses'),
(gen_random_uuid(), 'Manager', 'MANAGER', 'Manager role - can approve department expenses'),
(gen_random_uuid(), 'Finance Admin', 'FINANCE_ADMIN', 'Finance administrator - full system access'),
(gen_random_uuid(), 'Auditor', 'AUDITOR', 'Auditor role - read-only access for compliance');

-- Assign permissions to EMPLOYEE role
INSERT INTO role_permissions (role_id, permission_id)
SELECT
    r.id,
    p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.role_type = 'EMPLOYEE'
AND p.permission_type IN (
    'EXPENSE_READ_OWN',
    'EXPENSE_CREATE',
    'EXPENSE_UPDATE_OWN',
    'EXPENSE_DELETE_OWN',
    'SEGMENT_READ',
    'REPORT_READ_OWN',
    'DEPARTMENT_READ'
);

-- Assign permissions to MANAGER role (includes all employee permissions + approval)
INSERT INTO role_permissions (role_id, permission_id)
SELECT
    r.id,
    p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.role_type = 'MANAGER'
AND p.permission_type IN (
    'EXPENSE_READ_OWN',
    'EXPENSE_READ_DEPARTMENT',
    'EXPENSE_CREATE',
    'EXPENSE_UPDATE_OWN',
    'EXPENSE_DELETE_OWN',
    'EXPENSE_APPROVE_DEPARTMENT',
    'EXPENSE_REJECT',
    'SEGMENT_READ',
    'BUDGET_READ',
    'REPORT_READ_OWN',
    'REPORT_READ_DEPARTMENT',
    'REPORT_CREATE',
    'REPORT_EXPORT',
    'DEPARTMENT_READ',
    'USER_READ'
);

-- Assign permissions to FINANCE_ADMIN role (full access)
INSERT INTO role_permissions (role_id, permission_id)
SELECT
    r.id,
    p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.role_type = 'FINANCE_ADMIN';

-- Assign permissions to AUDITOR role (read-only access)
INSERT INTO role_permissions (role_id, permission_id)
SELECT
    r.id,
    p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.role_type = 'AUDITOR'
AND p.permission_type IN (
    'USER_READ',
    'EXPENSE_READ_ALL',
    'SEGMENT_READ',
    'BUDGET_READ',
    'REPORT_READ_ALL',
    'REPORT_EXPORT',
    'DEPARTMENT_READ',
    'SYSTEM_CONFIG_READ'
);
