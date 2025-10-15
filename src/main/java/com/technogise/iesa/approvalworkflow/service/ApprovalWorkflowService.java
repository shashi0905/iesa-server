package com.technogise.iesa.approvalworkflow.service;

import com.technogise.iesa.approvalworkflow.domain.*;
import com.technogise.iesa.approvalworkflow.dto.*;
import com.technogise.iesa.approvalworkflow.repository.*;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import com.technogise.iesa.usermanagement.domain.Role;
import com.technogise.iesa.usermanagement.domain.User;
import com.technogise.iesa.usermanagement.repository.RoleRepository;
import com.technogise.iesa.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApprovalWorkflowService {

    private final ApprovalWorkflowRepository workflowRepository;
    private final ApprovalStepRepository stepRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ApprovalWorkflowMapper mapper;

    // Workflow CRUD operations

    @Transactional(readOnly = true)
    public List<ApprovalWorkflowDto> getAllWorkflows() {
        return mapper.toWorkflowDtoList(workflowRepository.findAllNotDeleted());
    }

    @Transactional(readOnly = true)
    public List<ApprovalWorkflowDto> getAllActiveWorkflows() {
        return mapper.toWorkflowDtoList(workflowRepository.findAllActive());
    }

    @Transactional(readOnly = true)
    public ApprovalWorkflowDto getWorkflowById(UUID id) {
        ApprovalWorkflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found with id: " + id));
        return mapper.toDto(workflow);
    }

    @Transactional(readOnly = true)
    public ApprovalWorkflowDto getWorkflowByName(String name) {
        ApprovalWorkflow workflow = workflowRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found with name: " + name));
        return mapper.toDto(workflow);
    }

    public ApprovalWorkflowDto createWorkflow(CreateApprovalWorkflowRequest request) {
        log.info("Creating new approval workflow: {}", request.getName());

        // Check if workflow name already exists
        if (workflowRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Workflow with name '" + request.getName() + "' already exists");
        }

        // Create workflow
        ApprovalWorkflow workflow = ApprovalWorkflow.builder()
                .name(request.getName())
                .description(request.getDescription())
                .triggerConditions(request.getTriggerConditions())
                .isActive(true)
                .build();

        // Add steps if provided
        if (request.getSteps() != null) {
            for (ApprovalStepRequest stepRequest : request.getSteps()) {
                ApprovalStep step = createStepFromRequest(stepRequest);
                workflow.addStep(step);
            }
        }

        workflow = workflowRepository.save(workflow);
        log.info("Workflow created successfully with id: {}", workflow.getId());

        return mapper.toDto(workflow);
    }

    public ApprovalWorkflowDto updateWorkflow(UUID id, UpdateApprovalWorkflowRequest request) {
        log.info("Updating workflow with id: {}", id);

        ApprovalWorkflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found with id: " + id));

        // Update basic fields
        if (request.getName() != null) {
            // Check if new name conflicts with existing workflows
            if (!workflow.getName().equals(request.getName()) && workflowRepository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Workflow with name '" + request.getName() + "' already exists");
            }
            workflow.setName(request.getName());
        }

        if (request.getDescription() != null) {
            workflow.setDescription(request.getDescription());
        }

        if (request.getTriggerConditions() != null) {
            workflow.setTriggerConditions(request.getTriggerConditions());
        }

        if (request.getIsActive() != null) {
            workflow.setIsActive(request.getIsActive());
        }

        // Update steps if provided
        if (request.getSteps() != null) {
            // Remove all existing steps
            workflow.getSteps().clear();

            // Add new steps
            for (ApprovalStepRequest stepRequest : request.getSteps()) {
                ApprovalStep step = createStepFromRequest(stepRequest);
                workflow.addStep(step);
            }
        }

        workflow = workflowRepository.save(workflow);
        return mapper.toDto(workflow);
    }

    public void deleteWorkflow(UUID id) {
        log.info("Deleting workflow with id: {}", id);

        ApprovalWorkflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found with id: " + id));

        workflow.softDelete();
        workflowRepository.save(workflow);
    }

    public ApprovalWorkflowDto activateWorkflow(UUID id) {
        log.info("Activating workflow with id: {}", id);

        ApprovalWorkflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found with id: " + id));

        workflow.activate();
        workflow = workflowRepository.save(workflow);

        return mapper.toDto(workflow);
    }

    public ApprovalWorkflowDto deactivateWorkflow(UUID id) {
        log.info("Deactivating workflow with id: {}", id);

        ApprovalWorkflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found with id: " + id));

        workflow.deactivate();
        workflow = workflowRepository.save(workflow);

        return mapper.toDto(workflow);
    }

    // Step operations

    @Transactional(readOnly = true)
    public List<ApprovalStepDto> getWorkflowSteps(UUID workflowId) {
        return mapper.toStepDtoList(stepRepository.findByWorkflowIdOrderByStepOrder(workflowId));
    }

    @Transactional(readOnly = true)
    public List<ApprovalStepDto> getMandatorySteps(UUID workflowId) {
        return mapper.toStepDtoList(stepRepository.findMandatoryStepsByWorkflowId(workflowId));
    }

    // Private helper methods

    private ApprovalStep createStepFromRequest(ApprovalStepRequest request) {
        ApprovalStep.ApprovalStepBuilder builder = ApprovalStep.builder()
                .stepOrder(request.getStepOrder())
                .condition(request.getCondition())
                .isMandatory(request.getIsMandatory() != null ? request.getIsMandatory() : true)
                .stepName(request.getStepName());

        // Set approver role if provided
        if (request.getApproverRoleId() != null) {
            Role role = roleRepository.findById(request.getApproverRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getApproverRoleId()));
            builder.approverRole(role);
        }

        // Set approver user if provided
        if (request.getApproverUserId() != null) {
            User user = userRepository.findById(request.getApproverUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getApproverUserId()));
            builder.approverUser(user);
        }

        return builder.build();
    }
}
