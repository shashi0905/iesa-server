-- =====================================================
-- Migration V8: Create Approval Workflow Tables
-- Description: Creates tables for approval workflow module
-- Date: 2025-10-15
-- =====================================================

-- Create approval_workflows table
CREATE TABLE approval_workflows (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL UNIQUE,
    description VARCHAR(1000),
    trigger_conditions JSONB,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create approval_steps table
CREATE TABLE approval_steps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_id UUID NOT NULL,
    step_order INTEGER NOT NULL,
    approver_role_id UUID,
    approver_user_id UUID,
    condition VARCHAR(500),
    is_mandatory BOOLEAN NOT NULL DEFAULT true,
    step_name VARCHAR(200),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_step_workflow FOREIGN KEY (workflow_id)
        REFERENCES approval_workflows(id) ON DELETE CASCADE,
    CONSTRAINT fk_step_role FOREIGN KEY (approver_role_id)
        REFERENCES roles(id) ON DELETE SET NULL,
    CONSTRAINT fk_step_user FOREIGN KEY (approver_user_id)
        REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_step_approver CHECK (approver_role_id IS NOT NULL OR approver_user_id IS NOT NULL)
);

-- Create approval_actions table
CREATE TABLE approval_actions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    expense_id UUID NOT NULL,
    step_id UUID,
    approver_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL,
    comment VARCHAR(2000),
    action_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delegated_to_id UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_action_expense FOREIGN KEY (expense_id)
        REFERENCES expenses(id) ON DELETE CASCADE,
    CONSTRAINT fk_action_step FOREIGN KEY (step_id)
        REFERENCES approval_steps(id) ON DELETE SET NULL,
    CONSTRAINT fk_action_approver FOREIGN KEY (approver_id)
        REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_action_delegated_to FOREIGN KEY (delegated_to_id)
        REFERENCES users(id) ON DELETE SET NULL
);

-- Create comments table
CREATE TABLE comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    expense_id UUID NOT NULL,
    author_id UUID NOT NULL,
    content VARCHAR(2000) NOT NULL,
    is_internal BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_comment_expense FOREIGN KEY (expense_id)
        REFERENCES expenses(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id)
        REFERENCES users(id) ON DELETE RESTRICT
);

-- Create workflow_history table
CREATE TABLE workflow_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    expense_id UUID NOT NULL,
    from_status VARCHAR(50),
    to_status VARCHAR(50) NOT NULL,
    actor_id UUID NOT NULL,
    comment VARCHAR(2000),
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_history_expense FOREIGN KEY (expense_id)
        REFERENCES expenses(id) ON DELETE CASCADE,
    CONSTRAINT fk_history_actor FOREIGN KEY (actor_id)
        REFERENCES users(id) ON DELETE RESTRICT
);

-- Create indexes for approval_workflows table
CREATE INDEX idx_workflow_name ON approval_workflows(name);
CREATE INDEX idx_workflow_active ON approval_workflows(is_active);
CREATE INDEX idx_workflow_deleted ON approval_workflows(deleted_at);

-- Create indexes for approval_steps table
CREATE INDEX idx_step_workflow ON approval_steps(workflow_id);
CREATE INDEX idx_step_role ON approval_steps(approver_role_id);
CREATE INDEX idx_step_user ON approval_steps(approver_user_id);
CREATE INDEX idx_step_order ON approval_steps(workflow_id, step_order);

-- Create indexes for approval_actions table
CREATE INDEX idx_action_expense ON approval_actions(expense_id);
CREATE INDEX idx_action_approver ON approval_actions(approver_id);
CREATE INDEX idx_action_date ON approval_actions(action_date);
CREATE INDEX idx_action_type ON approval_actions(action);
CREATE INDEX idx_action_step ON approval_actions(step_id);
CREATE INDEX idx_action_delegated_to ON approval_actions(delegated_to_id);

-- Create indexes for comments table
CREATE INDEX idx_comment_expense ON comments(expense_id);
CREATE INDEX idx_comment_author ON comments(author_id);
CREATE INDEX idx_comment_created ON comments(created_at);
CREATE INDEX idx_comment_internal ON comments(is_internal);

-- Create indexes for workflow_history table
CREATE INDEX idx_history_expense ON workflow_history(expense_id);
CREATE INDEX idx_history_actor ON workflow_history(actor_id);
CREATE INDEX idx_history_timestamp ON workflow_history(timestamp);
CREATE INDEX idx_history_to_status ON workflow_history(to_status);
CREATE INDEX idx_history_from_status ON workflow_history(from_status);

-- Add comments for documentation
COMMENT ON TABLE approval_workflows IS 'Configurable approval workflows defining multi-step approval chains';
COMMENT ON TABLE approval_steps IS 'Individual steps within an approval workflow';
COMMENT ON TABLE approval_actions IS 'Records of approval actions taken on expenses';
COMMENT ON TABLE comments IS 'Collaboration comments on expenses';
COMMENT ON TABLE workflow_history IS 'Immutable audit trail of expense status transitions';

COMMENT ON COLUMN approval_workflows.trigger_conditions IS 'JSON conditions for when this workflow applies';
COMMENT ON COLUMN approval_steps.condition IS 'SpEL expression for conditional approval requirements';
COMMENT ON COLUMN approval_actions.action IS 'Action type: APPROVED, REJECTED, DELEGATED, COMMENTED';
COMMENT ON COLUMN comments.is_internal IS 'Whether comment is visible to approvers only (true) or submitter (false)';
COMMENT ON COLUMN workflow_history.timestamp IS 'When the status transition occurred';
