package com.technogise.iesa.approvalworkflow.repository;

import com.technogise.iesa.approvalworkflow.domain.ApprovalAction;
import com.technogise.iesa.approvalworkflow.domain.ApprovalActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ApprovalAction entity
 */
@Repository
public interface ApprovalActionRepository extends JpaRepository<ApprovalAction, UUID> {

    /**
     * Find all actions for an expense ordered by action date
     */
    @Query("SELECT a FROM ApprovalAction a WHERE a.expense.id = :expenseId ORDER BY a.actionDate DESC")
    List<ApprovalAction> findByExpenseIdOrderByActionDateDesc(UUID expenseId);

    /**
     * Find all actions by approver
     */
    @Query("SELECT a FROM ApprovalAction a WHERE a.approver.id = :approverId ORDER BY a.actionDate DESC")
    List<ApprovalAction> findByApproverId(UUID approverId);

    /**
     * Find actions by expense and action type
     */
    @Query("SELECT a FROM ApprovalAction a WHERE a.expense.id = :expenseId AND a.action = :actionType ORDER BY a.actionDate DESC")
    List<ApprovalAction> findByExpenseIdAndActionType(UUID expenseId, ApprovalActionType actionType);

    /**
     * Find the latest action for an expense
     */
    @Query("SELECT a FROM ApprovalAction a WHERE a.expense.id = :expenseId ORDER BY a.actionDate DESC LIMIT 1")
    Optional<ApprovalAction> findLatestByExpenseId(UUID expenseId);

    /**
     * Find actions for a specific step
     */
    @Query("SELECT a FROM ApprovalAction a WHERE a.step.id = :stepId ORDER BY a.actionDate DESC")
    List<ApprovalAction> findByStepId(UUID stepId);

    /**
     * Check if expense has been approved at a specific step
     */
    @Query("SELECT COUNT(a) > 0 FROM ApprovalAction a WHERE a.expense.id = :expenseId AND a.step.id = :stepId AND a.action = 'APPROVED'")
    boolean hasApprovedAtStep(UUID expenseId, UUID stepId);

    /**
     * Find actions within date range
     */
    @Query("SELECT a FROM ApprovalAction a WHERE a.actionDate BETWEEN :fromDate AND :toDate ORDER BY a.actionDate DESC")
    List<ApprovalAction> findByActionDateBetween(Instant fromDate, Instant toDate);

    /**
     * Count actions by approver and action type
     */
    @Query("SELECT COUNT(a) FROM ApprovalAction a WHERE a.approver.id = :approverId AND a.action = :actionType")
    long countByApproverIdAndActionType(UUID approverId, ApprovalActionType actionType);

    /**
     * Find pending delegations for a user
     */
    @Query("SELECT a FROM ApprovalAction a WHERE a.delegatedTo.id = :userId AND a.action = 'DELEGATED' ORDER BY a.actionDate DESC")
    List<ApprovalAction> findPendingDelegationsByUserId(UUID userId);
}
