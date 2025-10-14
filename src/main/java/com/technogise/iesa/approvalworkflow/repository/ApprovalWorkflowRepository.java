package com.technogise.iesa.approvalworkflow.repository;

import com.technogise.iesa.approvalworkflow.domain.ApprovalWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ApprovalWorkflow entity
 */
@Repository
public interface ApprovalWorkflowRepository extends JpaRepository<ApprovalWorkflow, UUID> {

    /**
     * Find all workflows that are not soft deleted
     */
    @Query("SELECT w FROM ApprovalWorkflow w WHERE w.deletedAt IS NULL")
    List<ApprovalWorkflow> findAllNotDeleted();

    /**
     * Find all active workflows
     */
    @Query("SELECT w FROM ApprovalWorkflow w WHERE w.isActive = true AND w.deletedAt IS NULL")
    List<ApprovalWorkflow> findAllActive();

    /**
     * Find workflow by name
     */
    @Query("SELECT w FROM ApprovalWorkflow w WHERE w.name = :name AND w.deletedAt IS NULL")
    Optional<ApprovalWorkflow> findByName(String name);

    /**
     * Check if workflow name exists
     */
    @Query("SELECT COUNT(w) > 0 FROM ApprovalWorkflow w WHERE w.name = :name AND w.deletedAt IS NULL")
    boolean existsByName(String name);

    /**
     * Find workflows with specific trigger conditions
     */
    @Query("SELECT w FROM ApprovalWorkflow w WHERE w.isActive = true AND w.deletedAt IS NULL " +
           "AND CAST(w.triggerConditions AS string) LIKE %:condition%")
    List<ApprovalWorkflow> findByTriggerCondition(String condition);
}
