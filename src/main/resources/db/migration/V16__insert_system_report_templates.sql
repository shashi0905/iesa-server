-- =====================================================
-- Migration V16: Insert System Report Templates
-- Description: Inserts pre-defined system report templates
-- Date: 2025-10-18
-- =====================================================

-- Insert system report templates
INSERT INTO report_templates (id, name, description, report_type, visualization_type, is_system_template, is_active, created_at)
VALUES
-- Expense Summary Report
(gen_random_uuid(),
 'Expense Summary',
 'Summary of expenses grouped by status',
 'EXPENSE_SUMMARY',
 'TABLE',
 true,
 true,
 CURRENT_TIMESTAMP),

-- Segment Analysis Report
(gen_random_uuid(),
 'Segment Analysis',
 'Expense breakdown by segment with visualization',
 'SEGMENT_ANALYSIS',
 'PIE',
 true,
 true,
 CURRENT_TIMESTAMP),

-- Budget vs Actual Variance
(gen_random_uuid(),
 'Budget Variance Analysis',
 'Comparison of budget allocated vs consumed',
 'BUDGET_VARIANCE',
 'BAR',
 true,
 true,
 CURRENT_TIMESTAMP),

-- Department Spending Report
(gen_random_uuid(),
 'Department Spending Trends',
 'Spending trends by department over time',
 'DEPARTMENT_SPENDING',
 'LINE',
 true,
 true,
 CURRENT_TIMESTAMP),

-- Top Spenders Report
(gen_random_uuid(),
 'Top Spenders',
 'List of top expense submitters',
 'TOP_SPENDERS',
 'TABLE',
 true,
 true,
 CURRENT_TIMESTAMP),

-- Pending Approvals Dashboard
(gen_random_uuid(),
 'Pending Approvals',
 'Expenses awaiting approval with aging information',
 'PENDING_APPROVALS',
 'TABLE',
 true,
 true,
 CURRENT_TIMESTAMP),

-- Expense Aging Report
(gen_random_uuid(),
 'Expense Aging Analysis',
 'Analysis of expense age by status',
 'EXPENSE_AGING',
 'BAR',
 true,
 true,
 CURRENT_TIMESTAMP),

-- Budget Utilization Report
(gen_random_uuid(),
 'Budget Utilization',
 'Budget utilization percentage by segment',
 'BUDGET_UTILIZATION',
 'DOUGHNUT',
 true,
 true,
 CURRENT_TIMESTAMP),

-- Trend Analysis Report
(gen_random_uuid(),
 'Expense Trend Analysis',
 'Monthly expense trends with forecasting',
 'TREND_ANALYSIS',
 'AREA',
 true,
 true,
 CURRENT_TIMESTAMP);

-- Add comments
COMMENT ON TABLE report_templates IS 'Contains 9 system-defined report templates for common reporting needs';
