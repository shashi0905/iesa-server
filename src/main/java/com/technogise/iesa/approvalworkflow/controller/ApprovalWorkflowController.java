package com.technogise.iesa.approvalworkflow.controller;

import com.technogise.iesa.approvalworkflow.dto.*;
import com.technogise.iesa.approvalworkflow.service.ApprovalWorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
@Slf4j
public class ApprovalWorkflowController {

    private final ApprovalWorkflowService workflowService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('WORKFLOW_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<ApprovalWorkflowDto>> getAllWorkflows() {
        return ResponseEntity.ok(workflowService.getAllWorkflows());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyAuthority('WORKFLOW_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<ApprovalWorkflowDto>> getAllActiveWorkflows() {
        return ResponseEntity.ok(workflowService.getAllActiveWorkflows());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WORKFLOW_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApprovalWorkflowDto> getWorkflowById(@PathVariable UUID id) {
        return ResponseEntity.ok(workflowService.getWorkflowById(id));
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyAuthority('WORKFLOW_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApprovalWorkflowDto> getWorkflowByName(@PathVariable String name) {
        return ResponseEntity.ok(workflowService.getWorkflowByName(name));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WORKFLOW_CREATE', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<ApprovalWorkflowDto> createWorkflow(@Valid @RequestBody CreateApprovalWorkflowRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowService.createWorkflow(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WORKFLOW_UPDATE', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<ApprovalWorkflowDto> updateWorkflow(@PathVariable UUID id,
                                                                @Valid @RequestBody UpdateApprovalWorkflowRequest request) {
        return ResponseEntity.ok(workflowService.updateWorkflow(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WORKFLOW_DELETE', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable UUID id) {
        workflowService.deleteWorkflow(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAnyAuthority('WORKFLOW_UPDATE', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<ApprovalWorkflowDto> activateWorkflow(@PathVariable UUID id) {
        return ResponseEntity.ok(workflowService.activateWorkflow(id));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyAuthority('WORKFLOW_UPDATE', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<ApprovalWorkflowDto> deactivateWorkflow(@PathVariable UUID id) {
        return ResponseEntity.ok(workflowService.deactivateWorkflow(id));
    }

    @GetMapping("/{id}/steps")
    @PreAuthorize("hasAnyAuthority('WORKFLOW_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<ApprovalStepDto>> getWorkflowSteps(@PathVariable UUID id) {
        return ResponseEntity.ok(workflowService.getWorkflowSteps(id));
    }

    @GetMapping("/{id}/steps/mandatory")
    @PreAuthorize("hasAnyAuthority('WORKFLOW_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<ApprovalStepDto>> getMandatorySteps(@PathVariable UUID id) {
        return ResponseEntity.ok(workflowService.getMandatorySteps(id));
    }
}
