package com.technogise.iesa.approvalworkflow.controller;

import com.technogise.iesa.approvalworkflow.dto.WorkflowHistoryDto;
import com.technogise.iesa.approvalworkflow.service.WorkflowHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workflow-history")
@RequiredArgsConstructor
@Slf4j
public class WorkflowHistoryController {

    private final WorkflowHistoryService historyService;

    @GetMapping("/expense/{expenseId}")
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<List<WorkflowHistoryDto>> getHistoryForExpense(@PathVariable UUID expenseId) {
        return ResponseEntity.ok(historyService.getHistoryForExpense(expenseId));
    }

    @GetMapping("/expense/{expenseId}/latest")
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<WorkflowHistoryDto> getLatestHistoryForExpense(@PathVariable UUID expenseId) {
        return ResponseEntity.ok(historyService.getLatestHistoryForExpense(expenseId));
    }

    @GetMapping("/actor/{actorId}")
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<WorkflowHistoryDto>> getHistoryByActor(@PathVariable UUID actorId) {
        return ResponseEntity.ok(historyService.getHistoryByActor(actorId));
    }

    @GetMapping("/approvals")
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<WorkflowHistoryDto>> getApprovalTransitions() {
        return ResponseEntity.ok(historyService.getApprovalTransitions());
    }

    @GetMapping("/rejections")
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<WorkflowHistoryDto>> getRejectionTransitions() {
        return ResponseEntity.ok(historyService.getRejectionTransitions());
    }
}
