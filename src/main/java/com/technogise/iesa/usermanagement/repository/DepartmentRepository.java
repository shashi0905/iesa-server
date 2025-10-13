package com.technogise.iesa.usermanagement.repository;

import com.technogise.iesa.usermanagement.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Department entity
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    /**
     * Find department by code (excluding soft-deleted)
     */
    @Query("SELECT d FROM Department d WHERE d.code = :code AND d.deletedAt IS NULL")
    Optional<Department> findByCode(@Param("code") String code);

    /**
     * Find department by name (excluding soft-deleted)
     */
    @Query("SELECT d FROM Department d WHERE d.name = :name AND d.deletedAt IS NULL")
    Optional<Department> findByName(@Param("name") String name);

    /**
     * Find all root departments (no parent, excluding soft-deleted)
     */
    @Query("SELECT d FROM Department d WHERE d.parentDepartment IS NULL AND d.deletedAt IS NULL")
    List<Department> findRootDepartments();

    /**
     * Find child departments (excluding soft-deleted)
     */
    @Query("SELECT d FROM Department d WHERE d.parentDepartment.id = :parentId AND d.deletedAt IS NULL")
    List<Department> findByParentDepartmentId(@Param("parentId") UUID parentId);

    /**
     * Find all active departments (excluding soft-deleted)
     */
    @Query("SELECT d FROM Department d WHERE d.isActive = true AND d.deletedAt IS NULL")
    List<Department> findAllActive();

    /**
     * Check if department code exists (excluding soft-deleted)
     */
    @Query("SELECT COUNT(d) > 0 FROM Department d WHERE d.code = :code AND d.deletedAt IS NULL")
    boolean existsByCode(@Param("code") String code);

    /**
     * Find departments managed by a user (excluding soft-deleted)
     */
    @Query("SELECT d FROM Department d WHERE d.manager.id = :managerId AND d.deletedAt IS NULL")
    List<Department> findByManagerId(@Param("managerId") UUID managerId);

}
