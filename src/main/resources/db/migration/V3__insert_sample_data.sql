-- Insert sample departments
INSERT INTO departments (id, name, code, description, parent_department_id, cost_center, is_active)
VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Engineering', 'ENG', 'Engineering Department', NULL, 'CC-ENG-001', TRUE),
('550e8400-e29b-41d4-a716-446655440002', 'Marketing', 'MKT', 'Marketing Department', NULL, 'CC-MKT-001', TRUE),
('550e8400-e29b-41d4-a716-446655440003', 'Finance', 'FIN', 'Finance Department', NULL, 'CC-FIN-001', TRUE),
('550e8400-e29b-41d4-a716-446655440004', 'Backend Team', 'ENG-BE', 'Backend Engineering Team', '550e8400-e29b-41d4-a716-446655440001', 'CC-ENG-BE-001', TRUE),
('550e8400-e29b-41d4-a716-446655440005', 'Frontend Team', 'ENG-FE', 'Frontend Engineering Team', '550e8400-e29b-41d4-a716-446655440001', 'CC-ENG-FE-001', TRUE);

-- Insert sample users
-- Password for all users is "password123" (BCrypt hashed)
INSERT INTO users (id, username, email, password_hash, first_name, last_name, phone_number, department_id, is_active, account_locked, password_expired, failed_login_attempts)
VALUES
-- Finance Admin
('650e8400-e29b-41d4-a716-446655440001', 'admin', 'admin@company.com', '$2y$10$6UYOXWnbgTOm56PgRb4dI.SZ5GQC7uRcCJgNcrz0vtuRWanZZuRgq', 'Admin', 'User', '+1234567890', '550e8400-e29b-41d4-a716-446655440003', TRUE, FALSE, FALSE, 0),

-- Managers
('650e8400-e29b-41d4-a716-446655440002', 'john.manager', 'john.manager@company.com', '$2y$10$6UYOXWnbgTOm56PgRb4dI.SZ5GQC7uRcCJgNcrz0vtuRWanZZuRgq', 'John', 'Manager', '+1234567891', '550e8400-e29b-41d4-a716-446655440001', TRUE, FALSE, FALSE, 0),
('650e8400-e29b-41d4-a716-446655440003', 'jane.marketing', 'jane.marketing@company.com', '$2y$10$6UYOXWnbgTOm56PgRb4dI.SZ5GQC7uRcCJgNcrz0vtuRWanZZuRgq', 'Jane', 'Marketing', '+1234567892', '550e8400-e29b-41d4-a716-446655440002', TRUE, FALSE, FALSE, 0),

-- Employees
('650e8400-e29b-41d4-a716-446655440004', 'alice.dev', 'alice.dev@company.com', '$2y$10$6UYOXWnbgTOm56PgRb4dI.SZ5GQC7uRcCJgNcrz0vtuRWanZZuRgq', 'Alice', 'Developer', '+1234567893', '550e8400-e29b-41d4-a716-446655440004', TRUE, FALSE, FALSE, 0),
('650e8400-e29b-41d4-a716-446655440005', 'bob.dev', 'bob.dev@company.com', '$2y$10$6UYOXWnbgTOm56PgRb4dI.SZ5GQC7uRcCJgNcrz0vtuRWanZZuRgq', 'Bob', 'Developer', '+1234567894', '550e8400-e29b-41d4-a716-446655440005', TRUE, FALSE, FALSE, 0),

-- Auditor
('650e8400-e29b-41d4-a716-446655440006', 'auditor', 'auditor@company.com', '$2y$10$6UYOXWnbgTOm56PgRb4dI.SZ5GQC7uRcCJgNcrz0vtuRWanZZuRgq', 'Audit', 'User', '+1234567895', '550e8400-e29b-41d4-a716-446655440003', TRUE, FALSE, FALSE, 0);

-- Update department managers
UPDATE departments SET manager_id = '650e8400-e29b-41d4-a716-446655440002' WHERE id = '550e8400-e29b-41d4-a716-446655440001';
UPDATE departments SET manager_id = '650e8400-e29b-41d4-a716-446655440003' WHERE id = '550e8400-e29b-41d4-a716-446655440002';
UPDATE departments SET manager_id = '650e8400-e29b-41d4-a716-446655440001' WHERE id = '550e8400-e29b-41d4-a716-446655440003';

-- Assign roles to users
-- Admin user gets FINANCE_ADMIN role
INSERT INTO user_roles (user_id, role_id)
SELECT '650e8400-e29b-41d4-a716-446655440001', id FROM roles WHERE role_type = 'FINANCE_ADMIN';

-- John and Jane get MANAGER role
INSERT INTO user_roles (user_id, role_id)
SELECT '650e8400-e29b-41d4-a716-446655440002', id FROM roles WHERE role_type = 'MANAGER';

INSERT INTO user_roles (user_id, role_id)
SELECT '650e8400-e29b-41d4-a716-446655440003', id FROM roles WHERE role_type = 'MANAGER';

-- Alice and Bob get EMPLOYEE role
INSERT INTO user_roles (user_id, role_id)
SELECT '650e8400-e29b-41d4-a716-446655440004', id FROM roles WHERE role_type = 'EMPLOYEE';

INSERT INTO user_roles (user_id, role_id)
SELECT '650e8400-e29b-41d4-a716-446655440005', id FROM roles WHERE role_type = 'EMPLOYEE';

-- Auditor gets AUDITOR role
INSERT INTO user_roles (user_id, role_id)
SELECT '650e8400-e29b-41d4-a716-446655440006', id FROM roles WHERE role_type = 'AUDITOR';
