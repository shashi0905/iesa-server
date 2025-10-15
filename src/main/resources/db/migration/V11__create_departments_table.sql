-- Create departments table
CREATE TABLE departments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    segment_id UUID REFERENCES segments(id),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_departments_code ON departments(code);
CREATE INDEX idx_departments_segment_id ON departments(segment_id);
CREATE INDEX idx_departments_is_active ON departments(is_active);
CREATE INDEX idx_departments_deleted_at ON departments(deleted_at);

-- Add comment
COMMENT ON TABLE departments IS 'Department definitions within segments/cost centers';

-- Insert sample departments
INSERT INTO departments (code, name, description, is_active) VALUES
('ENG', 'Engineering', 'Software Engineering Department', true),
('MKT', 'Marketing', 'Marketing and Communications Department', true),
('HR', 'Human Resources', 'HR and People Operations', true),
('FIN', 'Finance', 'Finance and Accounting Department', true),
('OPS', 'Operations', 'Operations and Facilities', true);
