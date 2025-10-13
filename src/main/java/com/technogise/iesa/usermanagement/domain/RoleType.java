package com.technogise.iesa.usermanagement.domain;

/**
 * Enum for predefined role types in the system
 */
public enum RoleType {
    /**
     * Employee role - can submit expenses
     */
    EMPLOYEE,

    /**
     * Manager role - can approve expenses for their department
     */
    MANAGER,

    /**
     * Finance Admin role - can configure settings, manage budgets, approve all expenses
     */
    FINANCE_ADMIN,

    /**
     * Auditor role - read-only access to all data for compliance
     */
    AUDITOR
}
