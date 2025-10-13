package com.technogise.iesa.usermanagement.domain;

import com.technogise.iesa.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User entity representing system users
 * Implements UserDetails for Spring Security integration
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity implements UserDetails {

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"),
        indexes = {
            @Index(name = "idx_user_roles_user", columnList = "user_id"),
            @Index(name = "idx_user_roles_role", columnList = "role_id")
        }
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "account_locked", nullable = false)
    @Builder.Default
    private Boolean accountLocked = false;

    @Column(name = "password_expired", nullable = false)
    @Builder.Default
    private Boolean passwordExpired = false;

    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    // UserDetails implementation for Spring Security

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Add role authorities
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleType().name()));

            // Add permission authorities
            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getPermissionType().name()));
            }
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.passwordExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive && !isDeleted();
    }

    // Business methods

    /**
     * Get user's full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Add a role to this user
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Remove a role from this user
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    /**
     * Check if user has a specific role type
     */
    public boolean hasRole(RoleType roleType) {
        return roles.stream()
            .anyMatch(role -> role.getRoleType() == roleType);
    }

    /**
     * Check if user has a specific permission
     */
    public boolean hasPermission(PermissionType permissionType) {
        return roles.stream()
            .anyMatch(role -> role.hasPermission(permissionType));
    }

    /**
     * Get all permission types for this user
     */
    public Set<PermissionType> getAllPermissions() {
        return roles.stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getPermissionType)
            .collect(Collectors.toSet());
    }

    /**
     * Increment failed login attempts
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.accountLocked = true;
        }
    }

    /**
     * Reset failed login attempts
     */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    /**
     * Lock user account
     */
    public void lockAccount() {
        this.accountLocked = true;
    }

    /**
     * Unlock user account
     */
    public void unlockAccount() {
        this.accountLocked = false;
        this.failedLoginAttempts = 0;
    }

}
