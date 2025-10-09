package com.company.iesa.usermanagement.repository;

import com.company.iesa.usermanagement.domain.Permission;
import com.company.iesa.usermanagement.domain.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Permission entity
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    /**
     * Find permission by permission type (excluding soft-deleted)
     */
    @Query("SELECT p FROM Permission p WHERE p.permissionType = :permissionType AND p.deletedAt IS NULL")
    Optional<Permission> findByPermissionType(@Param("permissionType") PermissionType permissionType);

    /**
     * Check if permission exists by permission type (excluding soft-deleted)
     */
    @Query("SELECT COUNT(p) > 0 FROM Permission p WHERE p.permissionType = :permissionType AND p.deletedAt IS NULL")
    boolean existsByPermissionType(@Param("permissionType") PermissionType permissionType);

}
