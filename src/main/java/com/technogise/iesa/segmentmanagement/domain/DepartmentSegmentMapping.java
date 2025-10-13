package com.technogise.iesa.segmentmanagement.domain;

import com.technogise.iesa.shared.domain.BaseEntity;
import com.technogise.iesa.usermanagement.domain.Department;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Mapping entity for department-specific segment visibility and default settings
 * Controls which segments are visible/accessible to which departments
 */
@Entity
@Table(name = "department_segment_mappings",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_dept_segment",
        columnNames = {"department_id", "segment_id"}
    ),
    indexes = {
        @Index(name = "idx_dept_segment_dept", columnList = "department_id"),
        @Index(name = "idx_dept_segment_segment", columnList = "segment_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DepartmentSegmentMapping extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "segment_id", nullable = false)
    private Segment segment;

    @Column(name = "is_visible", nullable = false)
    @Builder.Default
    private Boolean isVisible = true;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "can_create_expenses", nullable = false)
    @Builder.Default
    private Boolean canCreateExpenses = true;

    @Column(name = "can_view_reports", nullable = false)
    @Builder.Default
    private Boolean canViewReports = true;
}
