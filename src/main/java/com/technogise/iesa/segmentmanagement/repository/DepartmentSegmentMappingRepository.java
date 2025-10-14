package com.technogise.iesa.segmentmanagement.repository;

import com.technogise.iesa.segmentmanagement.domain.DepartmentSegmentMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for DepartmentSegmentMapping entity
 */
@Repository
public interface DepartmentSegmentMappingRepository extends JpaRepository<DepartmentSegmentMapping, UUID> {

    /**
     * Find mapping by department and segment (excluding soft-deleted)
     */
    @Query("SELECT dsm FROM DepartmentSegmentMapping dsm WHERE dsm.department.id = :departmentId AND dsm.segment.id = :segmentId AND dsm.deletedAt IS NULL")
    Optional<DepartmentSegmentMapping> findByDepartmentIdAndSegmentId(
        @Param("departmentId") UUID departmentId,
        @Param("segmentId") UUID segmentId
    );

    /**
     * Find all mappings for a department (excluding soft-deleted)
     */
    @Query("SELECT dsm FROM DepartmentSegmentMapping dsm WHERE dsm.department.id = :departmentId AND dsm.deletedAt IS NULL")
    List<DepartmentSegmentMapping> findByDepartmentId(@Param("departmentId") UUID departmentId);

    /**
     * Find all visible segments for a department (excluding soft-deleted)
     */
    @Query("SELECT dsm FROM DepartmentSegmentMapping dsm WHERE dsm.department.id = :departmentId AND dsm.isVisible = true AND dsm.deletedAt IS NULL")
    List<DepartmentSegmentMapping> findVisibleByDepartmentId(@Param("departmentId") UUID departmentId);

    /**
     * Find all mappings for a segment (excluding soft-deleted)
     */
    @Query("SELECT dsm FROM DepartmentSegmentMapping dsm WHERE dsm.segment.id = :segmentId AND dsm.deletedAt IS NULL")
    List<DepartmentSegmentMapping> findBySegmentId(@Param("segmentId") UUID segmentId);

    /**
     * Find default segment for a department (excluding soft-deleted)
     */
    @Query("SELECT dsm FROM DepartmentSegmentMapping dsm WHERE dsm.department.id = :departmentId AND dsm.isDefault = true AND dsm.deletedAt IS NULL")
    Optional<DepartmentSegmentMapping> findDefaultByDepartmentId(@Param("departmentId") UUID departmentId);

    /**
     * Check if mapping exists (excluding soft-deleted)
     */
    @Query("SELECT COUNT(dsm) > 0 FROM DepartmentSegmentMapping dsm WHERE dsm.department.id = :departmentId AND dsm.segment.id = :segmentId AND dsm.deletedAt IS NULL")
    boolean existsByDepartmentIdAndSegmentId(
        @Param("departmentId") UUID departmentId,
        @Param("segmentId") UUID segmentId
    );
}
