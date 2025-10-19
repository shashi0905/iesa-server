-- =====================================================
-- Migration V15: Create Reporting & Analytics Tables
-- Description: Creates tables for reporting and analytics module
-- Date: 2025-10-18
-- =====================================================

-- Create report_templates table
CREATE TABLE report_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    report_type VARCHAR(50) NOT NULL,
    visualization_type VARCHAR(50) NOT NULL,
    query_definition TEXT,
    configuration JSONB,
    is_system_template BOOLEAN NOT NULL DEFAULT false,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create reports table
CREATE TABLE reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    template_id UUID NOT NULL,
    created_by_user_id UUID NOT NULL,
    filters JSONB,
    start_date DATE,
    end_date DATE,
    scheduled_cron VARCHAR(100),
    is_favorite BOOLEAN NOT NULL DEFAULT false,
    last_executed_at TIMESTAMP WITHOUT TIME ZONE,
    execution_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_report_template FOREIGN KEY (template_id)
        REFERENCES report_templates(id) ON DELETE RESTRICT,
    CONSTRAINT fk_report_created_by_user FOREIGN KEY (created_by_user_id)
        REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_report_date_range CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date)
);

-- Create dashboards table
CREATE TABLE dashboards (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    owner_id UUID NOT NULL,
    layout JSONB,
    is_default BOOLEAN NOT NULL DEFAULT false,
    is_shared BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_dashboard_owner FOREIGN KEY (owner_id)
        REFERENCES users(id) ON DELETE RESTRICT
);

-- Create dashboard_widgets table
CREATE TABLE dashboard_widgets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dashboard_id UUID NOT NULL,
    report_id UUID NOT NULL,
    title VARCHAR(200),
    position JSONB,
    refresh_interval INTEGER,
    width INTEGER,
    height INTEGER,
    order_index INTEGER,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_widget_dashboard FOREIGN KEY (dashboard_id)
        REFERENCES dashboards(id) ON DELETE CASCADE,
    CONSTRAINT fk_widget_report FOREIGN KEY (report_id)
        REFERENCES reports(id) ON DELETE RESTRICT
);

-- Create analytics_snapshots table
CREATE TABLE analytics_snapshots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    snapshot_date DATE NOT NULL,
    dimension VARCHAR(50) NOT NULL,
    dimension_value VARCHAR(200) NOT NULL,
    total_expenses DECIMAL(19, 2) NOT NULL DEFAULT 0,
    expense_count INTEGER NOT NULL DEFAULT 0,
    approved_count INTEGER NOT NULL DEFAULT 0,
    pending_count INTEGER NOT NULL DEFAULT 0,
    rejected_count INTEGER NOT NULL DEFAULT 0,
    total_budget_allocated DECIMAL(19, 2),
    total_budget_consumed DECIMAL(19, 2),
    metadata JSONB,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_snapshot_unique UNIQUE (snapshot_date, dimension, dimension_value)
);

-- Create indexes for report_templates table
CREATE INDEX idx_report_template_type ON report_templates(report_type);
CREATE INDEX idx_report_template_system ON report_templates(is_system_template);
CREATE INDEX idx_report_template_active ON report_templates(is_active);
CREATE INDEX idx_report_template_deleted ON report_templates(deleted_at);

-- Create indexes for reports table
CREATE INDEX idx_report_created_by ON reports(created_by_user_id);
CREATE INDEX idx_report_template ON reports(template_id);
CREATE INDEX idx_report_date_range ON reports(start_date, end_date);
CREATE INDEX idx_report_favorite ON reports(created_by_user_id, is_favorite);
CREATE INDEX idx_report_deleted ON reports(deleted_at);

-- Create indexes for dashboards table
CREATE INDEX idx_dashboard_owner ON dashboards(owner_id);
CREATE INDEX idx_dashboard_default ON dashboards(owner_id, is_default);
CREATE INDEX idx_dashboard_shared ON dashboards(is_shared);
CREATE INDEX idx_dashboard_deleted ON dashboards(deleted_at);

-- Create indexes for dashboard_widgets table
CREATE INDEX idx_widget_dashboard ON dashboard_widgets(dashboard_id);
CREATE INDEX idx_widget_report ON dashboard_widgets(report_id);
CREATE INDEX idx_widget_deleted ON dashboard_widgets(deleted_at);

-- Create indexes for analytics_snapshots table
CREATE INDEX idx_snapshot_date_dimension ON analytics_snapshots(snapshot_date, dimension);
CREATE INDEX idx_snapshot_dimension_value ON analytics_snapshots(dimension, dimension_value);
CREATE INDEX idx_snapshot_date ON analytics_snapshots(snapshot_date);
CREATE INDEX idx_snapshot_deleted ON analytics_snapshots(deleted_at);

-- Add comments for documentation
COMMENT ON TABLE report_templates IS 'Pre-defined report templates for common reports';
COMMENT ON TABLE reports IS 'Custom reports created by users based on templates';
COMMENT ON TABLE dashboards IS 'User dashboards containing multiple report widgets';
COMMENT ON TABLE dashboard_widgets IS 'Widgets displayed on dashboards';
COMMENT ON TABLE analytics_snapshots IS 'Pre-aggregated analytics data for performance';

COMMENT ON COLUMN report_templates.report_type IS 'Type of report: EXPENSE_SUMMARY, SEGMENT_ANALYSIS, BUDGET_VARIANCE, etc.';
COMMENT ON COLUMN report_templates.visualization_type IS 'Visualization type: BAR, LINE, PIE, TABLE, etc.';
COMMENT ON COLUMN report_templates.is_system_template IS 'Whether this is a system-provided template';
COMMENT ON COLUMN reports.filters IS 'JSON filters applied to the report';
COMMENT ON COLUMN reports.scheduled_cron IS 'Cron expression for scheduled execution';
COMMENT ON COLUMN dashboards.layout IS 'JSON layout configuration for the dashboard';
COMMENT ON COLUMN dashboard_widgets.position IS 'JSON position and sizing information';
COMMENT ON COLUMN analytics_snapshots.dimension IS 'Dimension type: SEGMENT, DEPARTMENT, USER, etc.';
COMMENT ON COLUMN analytics_snapshots.metadata IS 'Additional metadata for the snapshot';
