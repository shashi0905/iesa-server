package com.technogise.iesa.usermanagement.repository;

import com.technogise.iesa.usermanagement.domain.Role;
import com.technogise.iesa.usermanagement.domain.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Role entity
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Find role by role type (excluding soft-deleted)
     */
    @Query("SELECT r FROM Role r WHERE r.roleType = :roleType AND r.deletedAt IS NULL")
    Optional<Role> findByRoleType(@Param("roleType") RoleType roleType);

    /**
     * Find role by name (excluding soft-deleted)
     */
    @Query("SELECT r FROM Role r WHERE r.name = :name AND r.deletedAt IS NULL")
    Optional<Role> findByName(@Param("name") String name);

    /**
     * Check if role exists by role type (excluding soft-deleted)
     */
    @Query("SELECT COUNT(r) > 0 FROM Role r WHERE r.roleType = :roleType AND r.deletedAt IS NULL")
    boolean existsByRoleType(@Param("roleType") RoleType roleType);

}
