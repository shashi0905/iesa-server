package com.technogise.iesa.usermanagement.domain;

import com.technogise.iesa.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Permission entity representing a specific action on a resource
 */
@Entity
@Table(name = "permissions", indexes = {
    @Index(name = "idx_permission_type", columnList = "permission_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Permission extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_type", nullable = false, unique = true, length = 100)
    private PermissionType permissionType;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "resource", nullable = false, length = 100)
    private String resource; // e.g., EXPENSE, BUDGET, REPORT

    @Column(name = "action", nullable = false, length = 50)
    private String action; // e.g., CREATE, READ, UPDATE, DELETE, APPROVE

}
