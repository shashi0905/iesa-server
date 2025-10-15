package com.technogise.iesa.approvalworkflow.repository;

import com.technogise.iesa.approvalworkflow.domain.WorkflowHistory;
import com.technogise.iesa.expensemanagement.domain.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for WorkflowHistory entity
 */
@Repository
public interface WorkflowHistoryRepository extends JpaRepository<WorkflowHistory, UUID> {

    /**
     * Find all history for an expense ordered by timestamp
     */
    @Query("SELECT h FROM WorkflowHistory h WHERE h.expense.id = :expenseId ORDER BY h.timestamp DESC")
    List<WorkflowHistory> findByExpenseIdOrderByTimestampDesc(UUID expenseId);

    /**
     * Find history by actor
     */
    @Query("SELECT h FROM WorkflowHistory h WHERE h.actor.id = :actorId ORDER BY h.timestamp DESC")
    List<WorkflowHistory> findByActorId(UUID actorId);

    /**
     * Find history by status transition
     */
    @Query("SELECT h FROM WorkflowHistory h WHERE h.fromStatus = :fromStatus AND h.toStatus = :toStatus ORDER BY h.timestamp DESC")
    List<WorkflowHistory> findByStatusTransition(ExpenseStatus fromStatus, ExpenseStatus toStatus);

    /**
     * Find the latest history entry for an expense
     */
    @Query("SELECT h FROM WorkflowHistory h WHERE h.expense.id = :expenseId ORDER BY h.timestamp DESC LIMIT 1")
    Optional<WorkflowHistory> findLatestByExpenseId(UUID expenseId);

    /**
     * Find history within date range
     */
    @Query("SELECT h FROM WorkflowHistory h WHERE h.timestamp BETWEEN :fromDate AND :toDate ORDER BY h.timestamp DESC")
    List<WorkflowHistory> findByTimestampBetween(Instant fromDate, Instant toDate);

    /**
     * Find all transitions to a specific status
     */
    @Query("SELECT h FROM WorkflowHistory h WHERE h.toStatus = :status ORDER BY h.timestamp DESC")
    List<WorkflowHistory> findByToStatus(ExpenseStatus status);

    /**
     * Count history entries for an expense
     */
    @Query("SELECT COUNT(h) FROM WorkflowHistory h WHERE h.expense.id = :expenseId")
    long countByExpenseId(UUID expenseId);

    /**
     * Find approval transitions (to APPROVED status)
     */
    @Query("SELECT h FROM WorkflowHistory h WHERE h.toStatus = 'APPROVED' ORDER BY h.timestamp DESC")
    List<WorkflowHistory> findApprovalTransitions();

    /**
     * Find rejection transitions (to REJECTED status)
     */
    @Query("SELECT h FROM WorkflowHistory h WHERE h.toStatus = 'REJECTED' ORDER BY h.timestamp DESC")
    List<WorkflowHistory> findRejectionTransitions();
}
