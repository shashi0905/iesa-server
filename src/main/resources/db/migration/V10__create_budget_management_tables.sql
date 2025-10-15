-- Create budgets table
CREATE TABLE budgets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    segment_id UUID REFERENCES segments(id),
    department_id UUID REFERENCES departments(id),
    period VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    allocated_amount DECIMAL(15, 2) NOT NULL,
    consumed_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT check_period CHECK (period IN ('MONTHLY', 'QUARTERLY', 'ANNUAL')),
    CONSTRAINT check_allocated_amount CHECK (allocated_amount > 0),
    CONSTRAINT check_consumed_amount CHECK (consumed_amount >= 0),
    CONSTRAINT check_dates CHECK (end_date > start_date)
);

-- Create budget_thresholds table
CREATE TABLE budget_thresholds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id UUID NOT NULL REFERENCES budgets(id) ON DELETE CASCADE,
    percentage DECIMAL(5, 2) NOT NULL,
    alert_enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_percentage CHECK (percentage >= 0 AND percentage <= 100),
    CONSTRAINT unique_budget_percentage UNIQUE (budget_id, percentage)
);

-- Create budget_threshold_recipients join table
CREATE TABLE budget_threshold_recipients (
    threshold_id UUID NOT NULL REFERENCES budget_thresholds(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (threshold_id, user_id)
);

-- Create budget_alerts table
CREATE TABLE budget_alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id UUID NOT NULL REFERENCES budgets(id) ON DELETE CASCADE,
    threshold_id UUID NOT NULL REFERENCES budget_thresholds(id) ON DELETE CASCADE,
    triggered_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    message VARCHAR(500) NOT NULL,
    is_acknowledged BOOLEAN NOT NULL DEFAULT false,
    acknowledged_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for budgets
CREATE INDEX idx_budgets_segment_id ON budgets(segment_id);
CREATE INDEX idx_budgets_department_id ON budgets(department_id);
CREATE INDEX idx_budgets_period ON budgets(period);
CREATE INDEX idx_budgets_is_active ON budgets(is_active);
CREATE INDEX idx_budgets_start_date ON budgets(start_date);
CREATE INDEX idx_budgets_end_date ON budgets(end_date);
CREATE INDEX idx_budgets_deleted_at ON budgets(deleted_at);

-- Create indexes for budget_thresholds
CREATE INDEX idx_budget_thresholds_budget_id ON budget_thresholds(budget_id);
CREATE INDEX idx_budget_thresholds_alert_enabled ON budget_thresholds(alert_enabled);

-- Create indexes for budget_alerts
CREATE INDEX idx_budget_alerts_budget_id ON budget_alerts(budget_id);
CREATE INDEX idx_budget_alerts_threshold_id ON budget_alerts(threshold_id);
CREATE INDEX idx_budget_alerts_triggered_date ON budget_alerts(triggered_date);
CREATE INDEX idx_budget_alerts_is_acknowledged ON budget_alerts(is_acknowledged);

-- Create indexes for budget_threshold_recipients
CREATE INDEX idx_budget_threshold_recipients_user_id ON budget_threshold_recipients(user_id);

-- Add comments
COMMENT ON TABLE budgets IS 'Budget definitions with allocated amounts and consumption tracking';
COMMENT ON TABLE budget_thresholds IS 'Threshold percentages that trigger alerts when budget consumption reaches them';
COMMENT ON TABLE budget_threshold_recipients IS 'Users who should be notified when a threshold is breached';
COMMENT ON TABLE budget_alerts IS 'Alerts generated when budget thresholds are exceeded';

COMMENT ON COLUMN budgets.period IS 'Budget period: MONTHLY, QUARTERLY, or ANNUAL';
COMMENT ON COLUMN budgets.consumed_amount IS 'Current consumed amount, updated when expenses are approved';
COMMENT ON COLUMN budget_thresholds.percentage IS 'Threshold percentage (0-100) of budget utilization';
COMMENT ON COLUMN budget_alerts.is_acknowledged IS 'Whether the alert has been acknowledged by a user';
