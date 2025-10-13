package com.technogise.iesa.segmentmanagement.repository;

import com.technogise.iesa.segmentmanagement.domain.Segment;
import com.technogise.iesa.segmentmanagement.domain.SegmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Segment entity
 */
@Repository
public interface SegmentRepository extends JpaRepository<Segment, UUID> {

    /**
     * Find segment by code (excluding soft-deleted)
     */
    @Query("SELECT s FROM Segment s WHERE s.code = :code AND s.deletedAt IS NULL")
    Optional<Segment> findByCode(@Param("code") String code);

    /**
     * Check if segment exists by code (excluding soft-deleted)
     */
    @Query("SELECT COUNT(s) > 0 FROM Segment s WHERE s.code = :code AND s.deletedAt IS NULL")
    boolean existsByCode(@Param("code") String code);

    /**
     * Find all active segments (excluding soft-deleted)
     */
    @Query("SELECT s FROM Segment s WHERE s.isActive = true AND s.deletedAt IS NULL ORDER BY s.displayOrder, s.name")
    List<Segment> findAllActive();

    /**
     * Find all segments by type (excluding soft-deleted)
     */
    @Query("SELECT s FROM Segment s WHERE s.segmentType = :type AND s.deletedAt IS NULL ORDER BY s.displayOrder, s.name")
    List<Segment> findBySegmentType(@Param("type") SegmentType type);

    /**
     * Find all root segments (no parent) (excluding soft-deleted)
     */
    @Query("SELECT s FROM Segment s WHERE s.parentSegment IS NULL AND s.deletedAt IS NULL ORDER BY s.displayOrder, s.name")
    List<Segment> findRootSegments();

    /**
     * Find all child segments of a parent (excluding soft-deleted)
     */
    @Query("SELECT s FROM Segment s WHERE s.parentSegment.id = :parentId AND s.deletedAt IS NULL ORDER BY s.displayOrder, s.name")
    List<Segment> findByParentSegmentId(@Param("parentId") UUID parentId);

    /**
     * Find all segments (excluding soft-deleted)
     */
    @Query("SELECT s FROM Segment s WHERE s.deletedAt IS NULL ORDER BY s.displayOrder, s.name")
    List<Segment> findAllNotDeleted();

    /**
     * Search segments by name or code (excluding soft-deleted)
     */
    @Query("SELECT s FROM Segment s WHERE (LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(s.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND s.deletedAt IS NULL ORDER BY s.name")
    List<Segment> searchSegments(@Param("searchTerm") String searchTerm);
}
