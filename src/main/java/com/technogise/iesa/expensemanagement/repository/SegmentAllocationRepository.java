package com.technogise.iesa.expensemanagement.repository;

import com.technogise.iesa.expensemanagement.domain.SegmentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for SegmentAllocation entity
 */
@Repository
public interface SegmentAllocationRepository extends JpaRepository<SegmentAllocation, UUID> {

    /**
     * Find all allocations for an expense (excluding soft-deleted)
     */
    @Query("SELECT sa FROM SegmentAllocation sa WHERE sa.expense.id = :expenseId AND sa.deletedAt IS NULL")
    List<SegmentAllocation> findByExpenseId(@Param("expenseId") UUID expenseId);

    /**
     * Find all allocations for a segment (excluding soft-deleted)
     */
    @Query("SELECT sa FROM SegmentAllocation sa WHERE sa.segment.id = :segmentId AND sa.deletedAt IS NULL")
    List<SegmentAllocation> findBySegmentId(@Param("segmentId") UUID segmentId);

    /**
     * Sum allocations for a segment (excluding soft-deleted)
     */
    @Query("SELECT COALESCE(SUM(sa.amount), 0) FROM SegmentAllocation sa WHERE sa.segment.id = :segmentId AND sa.expense.status = 'APPROVED' AND sa.deletedAt IS NULL")
    java.math.BigDecimal sumAmountBySegmentId(@Param("segmentId") UUID segmentId);

    /**
     * Delete all allocations for an expense
     */
    @Query("DELETE FROM SegmentAllocation sa WHERE sa.expense.id = :expenseId")
    void deleteByExpenseId(@Param("expenseId") UUID expenseId);
}
