-- =====================================================
-- Migration V4: Create Segment Management Tables
-- Description: Creates tables for segment management module
-- Date: 2025-10-13
-- =====================================================

-- Create segments table
CREATE TABLE segments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(1000),
    segment_type VARCHAR(50) NOT NULL,
    parent_segment_id UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    display_order INTEGER DEFAULT 0,
    metadata JSONB,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_segment_parent FOREIGN KEY (parent_segment_id)
        REFERENCES segments(id) ON DELETE SET NULL
);

-- Create department_segment_mappings table
CREATE TABLE department_segment_mappings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    department_id UUID NOT NULL,
    segment_id UUID NOT NULL,
    is_visible BOOLEAN NOT NULL DEFAULT true,
    is_default BOOLEAN NOT NULL DEFAULT false,
    can_create_expenses BOOLEAN NOT NULL DEFAULT true,
    can_view_reports BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_dept_segment_dept FOREIGN KEY (department_id)
        REFERENCES departments(id) ON DELETE CASCADE,
    CONSTRAINT fk_dept_segment_segment FOREIGN KEY (segment_id)
        REFERENCES segments(id) ON DELETE CASCADE,
    CONSTRAINT uk_dept_segment UNIQUE (department_id, segment_id)
);

-- Create indexes for segments table
CREATE INDEX idx_segment_code ON segments(code);
CREATE INDEX idx_segment_name ON segments(name);
CREATE INDEX idx_segment_type ON segments(segment_type);
CREATE INDEX idx_segment_parent ON segments(parent_segment_id);
CREATE INDEX idx_segment_active ON segments(is_active);

-- Create indexes for department_segment_mappings table
CREATE INDEX idx_dept_segment_dept ON department_segment_mappings(department_id);
CREATE INDEX idx_dept_segment_segment ON department_segment_mappings(segment_id);
CREATE INDEX idx_dept_segment_visible ON department_segment_mappings(is_visible);
CREATE INDEX idx_dept_segment_default ON department_segment_mappings(is_default);

-- Add comments for documentation
COMMENT ON TABLE segments IS 'Stores segment definitions for expense categorization';
COMMENT ON TABLE department_segment_mappings IS 'Maps segments to departments with visibility and permission rules';

COMMENT ON COLUMN segments.segment_type IS 'Type of segment: COST_CENTER, PROJECT, CATEGORY, DEPARTMENT, LOCATION';
COMMENT ON COLUMN segments.metadata IS 'Additional metadata stored as JSON';
COMMENT ON COLUMN department_segment_mappings.is_visible IS 'Whether segment is visible to department';
COMMENT ON COLUMN department_segment_mappings.is_default IS 'Whether this is the default segment for the department';
COMMENT ON COLUMN department_segment_mappings.can_create_expenses IS 'Whether department can create expenses for this segment';
COMMENT ON COLUMN department_segment_mappings.can_view_reports IS 'Whether department can view reports for this segment';
