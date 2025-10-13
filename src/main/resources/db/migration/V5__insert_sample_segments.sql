-- =====================================================
-- Migration V5: Insert Sample Segments
-- Description: Inserts sample segment data for testing
-- Date: 2025-10-13
-- =====================================================

-- Insert root segments for different types

-- COST_CENTER segments
INSERT INTO segments (id, name, code, description, segment_type, parent_segment_id, is_active, display_order) VALUES
    (gen_random_uuid(), 'Engineering', 'CC-ENG', 'Engineering department cost center', 'COST_CENTER', NULL, true, 1),
    (gen_random_uuid(), 'Marketing', 'CC-MKT', 'Marketing department cost center', 'COST_CENTER', NULL, true, 2),
    (gen_random_uuid(), 'Sales', 'CC-SALES', 'Sales department cost center', 'COST_CENTER', NULL, true, 3),
    (gen_random_uuid(), 'Operations', 'CC-OPS', 'Operations department cost center', 'COST_CENTER', NULL, true, 4);

-- PROJECT segments
INSERT INTO segments (id, name, code, description, segment_type, parent_segment_id, is_active, display_order) VALUES
    (gen_random_uuid(), 'IESA Platform', 'PRJ-IESA', 'Invoice and Expense Segmentation App project', 'PROJECT', NULL, true, 1),
    (gen_random_uuid(), 'Mobile App Redesign', 'PRJ-MOBILE', 'Mobile application redesign project', 'PROJECT', NULL, true, 2),
    (gen_random_uuid(), 'Cloud Migration', 'PRJ-CLOUD', 'Infrastructure cloud migration project', 'PROJECT', NULL, true, 3),
    (gen_random_uuid(), 'Q1 Marketing Campaign', 'PRJ-MKT-Q1', 'Q1 2025 marketing campaign', 'PROJECT', NULL, true, 4);

-- CATEGORY segments
INSERT INTO segments (id, name, code, description, segment_type, parent_segment_id, is_active, display_order) VALUES
    (gen_random_uuid(), 'Travel', 'CAT-TRAVEL', 'Travel and accommodation expenses', 'CATEGORY', NULL, true, 1),
    (gen_random_uuid(), 'Software & Tools', 'CAT-SOFTWARE', 'Software licenses and tools', 'CATEGORY', NULL, true, 2),
    (gen_random_uuid(), 'Office Supplies', 'CAT-SUPPLIES', 'Office supplies and equipment', 'CATEGORY', NULL, true, 3),
    (gen_random_uuid(), 'Training & Development', 'CAT-TRAINING', 'Employee training and development', 'CATEGORY', NULL, true, 4),
    (gen_random_uuid(), 'Marketing Materials', 'CAT-MKT-MAT', 'Marketing and promotional materials', 'CATEGORY', NULL, true, 5);

-- LOCATION segments
INSERT INTO segments (id, name, code, description, segment_type, parent_segment_id, is_active, display_order) VALUES
    (gen_random_uuid(), 'San Francisco Office', 'LOC-SF', 'San Francisco headquarters', 'LOCATION', NULL, true, 1),
    (gen_random_uuid(), 'New York Office', 'LOC-NY', 'New York regional office', 'LOCATION', NULL, true, 2),
    (gen_random_uuid(), 'London Office', 'LOC-LON', 'London European office', 'LOCATION', NULL, true, 3),
    (gen_random_uuid(), 'Remote', 'LOC-REMOTE', 'Remote/distributed team', 'LOCATION', NULL, true, 4);

-- Insert some child segments (sub-projects under IESA Platform)
WITH iesa_project AS (
    SELECT id FROM segments WHERE code = 'PRJ-IESA'
)
INSERT INTO segments (id, name, code, description, segment_type, parent_segment_id, is_active, display_order)
SELECT
    gen_random_uuid(),
    'Backend Development',
    'PRJ-IESA-BE',
    'Backend API development for IESA',
    'PROJECT',
    iesa_project.id,
    true,
    1
FROM iesa_project;

WITH iesa_project AS (
    SELECT id FROM segments WHERE code = 'PRJ-IESA'
)
INSERT INTO segments (id, name, code, description, segment_type, parent_segment_id, is_active, display_order)
SELECT
    gen_random_uuid(),
    'Frontend Development',
    'PRJ-IESA-FE',
    'Frontend UI development for IESA',
    'PROJECT',
    iesa_project.id,
    true,
    2
FROM iesa_project;

-- Create department-segment mappings
-- Map Engineering department to relevant segments
WITH
    eng_dept AS (SELECT id FROM departments WHERE name = 'Engineering'),
    segments_for_eng AS (
        SELECT id FROM segments WHERE code IN (
            'CC-ENG', 'PRJ-IESA', 'PRJ-IESA-BE', 'PRJ-IESA-FE',
            'PRJ-MOBILE', 'PRJ-CLOUD', 'CAT-SOFTWARE', 'CAT-TRAINING',
            'LOC-SF', 'LOC-REMOTE'
        )
    )
INSERT INTO department_segment_mappings (id, department_id, segment_id, is_visible, is_default, can_create_expenses, can_view_reports)
SELECT
    gen_random_uuid(),
    eng_dept.id,
    segments_for_eng.id,
    true,
    CASE WHEN segments.code = 'CC-ENG' THEN true ELSE false END,
    true,
    true
FROM eng_dept, segments_for_eng
JOIN segments ON segments.id = segments_for_eng.id;

-- Map Marketing department to relevant segments
WITH
    mkt_dept AS (SELECT id FROM departments WHERE name = 'Marketing'),
    segments_for_mkt AS (
        SELECT id FROM segments WHERE code IN (
            'CC-MKT', 'PRJ-MKT-Q1', 'CAT-TRAVEL', 'CAT-MKT-MAT',
            'CAT-TRAINING', 'LOC-SF', 'LOC-NY', 'LOC-REMOTE'
        )
    )
INSERT INTO department_segment_mappings (id, department_id, segment_id, is_visible, is_default, can_create_expenses, can_view_reports)
SELECT
    gen_random_uuid(),
    mkt_dept.id,
    segments_for_mkt.id,
    true,
    CASE WHEN segments.code = 'CC-MKT' THEN true ELSE false END,
    true,
    true
FROM mkt_dept, segments_for_mkt
JOIN segments ON segments.id = segments_for_mkt.id;

-- Map Finance department to all segments (they need visibility into everything)
WITH
    finance_dept AS (SELECT id FROM departments WHERE name = 'Finance'),
    all_segments AS (SELECT id FROM segments WHERE deleted_at IS NULL)
INSERT INTO department_segment_mappings (id, department_id, segment_id, is_visible, is_default, can_create_expenses, can_view_reports)
SELECT
    gen_random_uuid(),
    finance_dept.id,
    all_segments.id,
    true,
    false,
    true,
    true
FROM finance_dept, all_segments;
