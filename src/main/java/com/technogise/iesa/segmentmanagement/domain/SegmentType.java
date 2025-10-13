package com.technogise.iesa.segmentmanagement.domain;

/**
 * Enum representing different types of segments
 */
public enum SegmentType {
    COST_CENTER("Cost Center", "Organizational cost centers for tracking expenses"),
    PROJECT("Project", "Project-based expense tracking"),
    CATEGORY("Category", "General expense categories"),
    DEPARTMENT("Department", "Department-level expense allocation"),
    LOCATION("Location", "Location-based expense tracking");

    private final String displayName;
    private final String description;

    SegmentType(String displayName, String description) {
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
