package com.company.iesa.usermanagement.repository;

import com.company.iesa.usermanagement.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by username (excluding soft-deleted)
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.deletedAt IS NULL")
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * Find user by email (excluding soft-deleted)
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Find user by username or email (excluding soft-deleted)
     */
    @Query("SELECT u FROM User u WHERE (u.username = :identifier OR u.email = :identifier) AND u.deletedAt IS NULL")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    /**
     * Check if username exists (excluding soft-deleted)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.deletedAt IS NULL")
    boolean existsByUsername(@Param("username") String username);

    /**
     * Check if email exists (excluding soft-deleted)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    boolean existsByEmail(@Param("email") String email);

    /**
     * Find all users in a department (excluding soft-deleted)
     */
    @Query("SELECT u FROM User u WHERE u.department.id = :departmentId AND u.deletedAt IS NULL")
    List<User> findByDepartmentId(@Param("departmentId") UUID departmentId);

    /**
     * Find all active users (excluding soft-deleted)
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.deletedAt IS NULL")
    List<User> findAllActive();

    /**
     * Find all users with a specific role (excluding soft-deleted)
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.id = :roleId AND u.deletedAt IS NULL")
    List<User> findByRoleId(@Param("roleId") UUID roleId);

}
