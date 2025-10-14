package com.technogise.iesa.approvalworkflow.repository;

import com.technogise.iesa.approvalworkflow.domain.ApprovalStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for ApprovalStep entity
 */
@Repository
public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, UUID> {

    /**
     * Find all steps for a workflow ordered by step order
     */
    @Query("SELECT s FROM ApprovalStep s WHERE s.workflow.id = :workflowId AND s.deletedAt IS NULL ORDER BY s.stepOrder ASC")
    List<ApprovalStep> findByWorkflowIdOrderByStepOrder(UUID workflowId);

    /**
     * Find steps by role
     */
    @Query("SELECT s FROM ApprovalStep s WHERE s.approverRole.id = :roleId AND s.deletedAt IS NULL")
    List<ApprovalStep> findByApproverRoleId(UUID roleId);

    /**
     * Find steps by specific user
     */
    @Query("SELECT s FROM ApprovalStep s WHERE s.approverUser.id = :userId AND s.deletedAt IS NULL")
    List<ApprovalStep> findByApproverUserId(UUID userId);

    /**
     * Find mandatory steps for a workflow
     */
    @Query("SELECT s FROM ApprovalStep s WHERE s.workflow.id = :workflowId AND s.isMandatory = true AND s.deletedAt IS NULL ORDER BY s.stepOrder ASC")
    List<ApprovalStep> findMandatoryStepsByWorkflowId(UUID workflowId);

    /**
     * Count steps in a workflow
     */
    @Query("SELECT COUNT(s) FROM ApprovalStep s WHERE s.workflow.id = :workflowId AND s.deletedAt IS NULL")
    long countByWorkflowId(UUID workflowId);
}
