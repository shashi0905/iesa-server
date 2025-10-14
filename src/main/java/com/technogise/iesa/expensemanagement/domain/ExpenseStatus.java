package com.technogise.iesa.expensemanagement.domain;

/**
 * Enum representing different expense statuses
 */
public enum ExpenseStatus {
    DRAFT("Draft", "Expense is being prepared"),
    SUBMITTED("Submitted", "Expense submitted for approval"),
    APPROVED("Approved", "Expense has been approved"),
    REJECTED("Rejected", "Expense has been rejected"),
    PAID("Paid", "Expense has been paid");

    private final String displayName;
    private final String description;

    ExpenseStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
