package com.technogise.iesa.approvalworkflow.controller;

import com.technogise.iesa.approvalworkflow.dto.ApprovalActionDto;
import com.technogise.iesa.approvalworkflow.dto.CreateApprovalActionRequest;
import com.technogise.iesa.approvalworkflow.service.ApprovalActionService;
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
@RequestMapping("/api/v1/approval-actions")
@RequiredArgsConstructor
@Slf4j
public class ApprovalActionController {

    private final ApprovalActionService actionService;

    @GetMapping("/expense/{expenseId}")
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<List<ApprovalActionDto>> getActionsForExpense(@PathVariable UUID expenseId) {
        return ResponseEntity.ok(actionService.getActionsForExpense(expenseId));
    }

    @GetMapping("/approver/{approverId}")
    @PreAuthorize("hasAnyAuthority('EXPENSE_APPROVE', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<ApprovalActionDto>> getActionsByApprover(@PathVariable UUID approverId) {
        return ResponseEntity.ok(actionService.getActionsByApprover(approverId));
    }

    @GetMapping("/expense/{expenseId}/latest")
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<ApprovalActionDto> getLatestActionForExpense(@PathVariable UUID expenseId) {
        return ResponseEntity.ok(actionService.getLatestActionForExpense(expenseId));
    }

    @GetMapping("/delegations/{userId}")
    @PreAuthorize("hasAnyAuthority('EXPENSE_APPROVE', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<ApprovalActionDto>> getPendingDelegations(@PathVariable UUID userId) {
        return ResponseEntity.ok(actionService.getPendingDelegations(userId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('EXPENSE_APPROVE', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApprovalActionDto> createApprovalAction(@Valid @RequestBody CreateApprovalActionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(actionService.createApprovalAction(request));
    }
}
