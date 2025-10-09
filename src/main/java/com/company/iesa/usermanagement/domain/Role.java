package com.company.iesa.usermanagement.domain;

import com.company.iesa.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Role entity representing a set of permissions
 */
@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_role_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Role extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false, unique = true, length = 50)
    private RoleType roleType;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id"),
        indexes = {
            @Index(name = "idx_role_permissions_role", columnList = "role_id"),
            @Index(name = "idx_role_permissions_permission", columnList = "permission_id")
        }
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    /**
     * Add a permission to this role
     */
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    /**
     * Remove a permission from this role
     */
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    /**
     * Check if role has a specific permission
     */
    public boolean hasPermission(PermissionType permissionType) {
        return permissions.stream()
            .anyMatch(p -> p.getPermissionType() == permissionType);
    }

}
