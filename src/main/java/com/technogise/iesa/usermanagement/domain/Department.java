package com.technogise.iesa.usermanagement.domain;

import com.technogise.iesa.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Department entity representing organizational units
 * Supports hierarchical structure with parent-child relationships
 */
@Entity
@Table(name = "departments", indexes = {
    @Index(name = "idx_department_name", columnList = "name"),
    @Index(name = "idx_department_code", columnList = "code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Department extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "description", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_department_id")
    private Department parentDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @Column(name = "cost_center", length = 50)
    private String costCenter;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Check if this is a root department (no parent)
     */
    public boolean isRootDepartment() {
        return this.parentDepartment == null;
    }

}
