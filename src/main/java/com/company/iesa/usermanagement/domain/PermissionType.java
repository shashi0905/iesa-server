package com.company.iesa.usermanagement.domain;

/**
 * Enum for permission types
 * Defines actions that can be performed on resources
 */
public enum PermissionType {
    // User Management
    USER_READ,
    USER_CREATE,
    USER_UPDATE,
    USER_DELETE,

    // Expense Management
    EXPENSE_READ_OWN,
    EXPENSE_READ_DEPARTMENT,
    EXPENSE_READ_ALL,
    EXPENSE_CREATE,
    EXPENSE_UPDATE_OWN,
    EXPENSE_UPDATE_ALL,
    EXPENSE_DELETE_OWN,
    EXPENSE_DELETE_ALL,

    // Approval
    EXPENSE_APPROVE_DEPARTMENT,
    EXPENSE_APPROVE_ALL,
    EXPENSE_REJECT,

    // Segment Management
    SEGMENT_READ,
    SEGMENT_CREATE,
    SEGMENT_UPDATE,
    SEGMENT_DELETE,

    // Budget Management
    BUDGET_READ,
    BUDGET_CREATE,
    BUDGET_UPDATE,
    BUDGET_DELETE,

    // Reporting
    REPORT_READ_OWN,
    REPORT_READ_DEPARTMENT,
    REPORT_READ_ALL,
    REPORT_CREATE,
    REPORT_EXPORT,

    // Department Management
    DEPARTMENT_READ,
    DEPARTMENT_CREATE,
    DEPARTMENT_UPDATE,
    DEPARTMENT_DELETE,

    // System Configuration
    SYSTEM_CONFIG_READ,
    SYSTEM_CONFIG_UPDATE
}
