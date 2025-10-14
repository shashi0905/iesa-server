package com.technogise.iesa.expensemanagement.controller;

import com.technogise.iesa.expensemanagement.domain.ExpenseStatus;
import com.technogise.iesa.expensemanagement.dto.*;
import com.technogise.iesa.expensemanagement.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@Slf4j
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<ExpenseDto>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<ExpenseDto> getExpenseById(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    @GetMapping("/submitter/{submitterId}")
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<ExpenseDto>> getExpensesBySubmitter(@PathVariable UUID submitterId) {
        return ResponseEntity.ok(expenseService.getExpensesBySubmitter(submitterId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<ExpenseDto>> getExpensesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(expenseService.getExpensesByStatus(ExpenseStatus.valueOf(status)));
    }

    @GetMapping("/pending-approvals")
    @PreAuthorize("hasAnyAuthority('EXPENSE_APPROVE', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<ExpenseDto>> getPendingApprovals() {
        return ResponseEntity.ok(expenseService.getPendingApprovals());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('EXPENSE_CREATE', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<ExpenseDto> createExpense(@Valid @RequestBody CreateExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.createExpense(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EXPENSE_UPDATE', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<ExpenseDto> updateExpense(@PathVariable UUID id, @Valid @RequestBody UpdateExpenseRequest request) {
        return ResponseEntity.ok(expenseService.updateExpense(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EXPENSE_DELETE', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyAuthority('EXPENSE_CREATE', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<ExpenseDto> submitExpense(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseService.submitExpense(id));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('EXPENSE_APPROVE', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ExpenseDto> approveExpense(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseService.approveExpense(id));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('EXPENSE_APPROVE', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ExpenseDto> rejectExpense(@PathVariable UUID id, @RequestBody Map<String, String> payload) {
        String reason = payload.getOrDefault("reason", "");
        return ResponseEntity.ok(expenseService.rejectExpense(id, reason));
    }
}
