package com.technogise.iesa.budgetmanagement.controller;

import com.technogise.iesa.budgetmanagement.domain.BudgetPeriod;
import com.technogise.iesa.budgetmanagement.dto.BudgetDto;
import com.technogise.iesa.budgetmanagement.dto.CreateBudgetRequest;
import com.technogise.iesa.budgetmanagement.dto.UpdateBudgetRequest;
import com.technogise.iesa.budgetmanagement.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<BudgetDto>> getAllBudgets() {
        return ResponseEntity.ok(budgetService.getAllBudgets());
    }

    @GetMapping("/active")
    public ResponseEntity<List<BudgetDto>> getAllActiveBudgets() {
        return ResponseEntity.ok(budgetService.getAllActiveBudgets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetDto> getBudgetById(@PathVariable UUID id) {
        return ResponseEntity.ok(budgetService.getBudgetById(id));
    }

    @GetMapping("/segment/{segmentId}")
    public ResponseEntity<List<BudgetDto>> getActiveBudgetsBySegment(@PathVariable UUID segmentId) {
        return ResponseEntity.ok(budgetService.getActiveBudgetsBySegment(segmentId));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<BudgetDto>> getActiveBudgetsByDepartment(@PathVariable UUID departmentId) {
        return ResponseEntity.ok(budgetService.getActiveBudgetsByDepartment(departmentId));
    }

    @GetMapping("/period/{period}")
    public ResponseEntity<List<BudgetDto>> getBudgetsByPeriod(@PathVariable BudgetPeriod period) {
        return ResponseEntity.ok(budgetService.getBudgetsByPeriod(period));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<BudgetDto>> getBudgetsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(budgetService.getBudgetsByDateRange(startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<BudgetDto> createBudget(@Valid @RequestBody CreateBudgetRequest request) {
        BudgetDto createdBudget = budgetService.createBudget(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBudget);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetDto> updateBudget(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBudgetRequest request) {
        return ResponseEntity.ok(budgetService.updateBudget(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable UUID id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<BudgetDto> activateBudget(@PathVariable UUID id) {
        return ResponseEntity.ok(budgetService.activateBudget(id));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<BudgetDto> deactivateBudget(@PathVariable UUID id) {
        return ResponseEntity.ok(budgetService.deactivateBudget(id));
    }

    @PutMapping("/{id}/consumption")
    public ResponseEntity<BudgetDto> updateConsumption(
            @PathVariable UUID id,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(budgetService.updateConsumption(id, amount));
    }

    @GetMapping("/{id}/remaining")
    public ResponseEntity<BigDecimal> getRemainingAmount(@PathVariable UUID id) {
        return ResponseEntity.ok(budgetService.getRemainingAmount(id));
    }

    @GetMapping("/{id}/utilization")
    public ResponseEntity<BigDecimal> getBudgetUtilization(@PathVariable UUID id) {
        return ResponseEntity.ok(budgetService.getBudgetUtilization(id));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkBudgetAvailability(
            @PathVariable UUID id,
            @RequestParam BigDecimal requestedAmount) {
        return ResponseEntity.ok(budgetService.checkBudgetAvailability(id, requestedAmount));
    }
}
