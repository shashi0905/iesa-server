package com.technogise.iesa.budgetmanagement.controller;

import com.technogise.iesa.budgetmanagement.dto.BudgetThresholdDto;
import com.technogise.iesa.budgetmanagement.dto.CreateThresholdRequest;
import com.technogise.iesa.budgetmanagement.dto.UpdateThresholdRequest;
import com.technogise.iesa.budgetmanagement.service.BudgetThresholdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budget-thresholds")
@RequiredArgsConstructor
public class BudgetThresholdController {

    private final BudgetThresholdService thresholdService;

    @GetMapping
    public ResponseEntity<List<BudgetThresholdDto>> getAllThresholds() {
        return ResponseEntity.ok(thresholdService.getAllThresholds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetThresholdDto> getThresholdById(@PathVariable UUID id) {
        return ResponseEntity.ok(thresholdService.getThresholdById(id));
    }

    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<BudgetThresholdDto>> getThresholdsByBudget(@PathVariable UUID budgetId) {
        return ResponseEntity.ok(thresholdService.getThresholdsByBudget(budgetId));
    }

    @GetMapping("/enabled")
    public ResponseEntity<List<BudgetThresholdDto>> getEnabledThresholds() {
        return ResponseEntity.ok(thresholdService.getEnabledThresholds());
    }

    @PostMapping
    public ResponseEntity<BudgetThresholdDto> createThreshold(@Valid @RequestBody CreateThresholdRequest request) {
        BudgetThresholdDto createdThreshold = thresholdService.createThreshold(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdThreshold);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetThresholdDto> updateThreshold(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateThresholdRequest request) {
        return ResponseEntity.ok(thresholdService.updateThreshold(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThreshold(@PathVariable UUID id) {
        thresholdService.deleteThreshold(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<BudgetThresholdDto> enableThreshold(@PathVariable UUID id) {
        return ResponseEntity.ok(thresholdService.enableThreshold(id));
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<BudgetThresholdDto> disableThreshold(@PathVariable UUID id) {
        return ResponseEntity.ok(thresholdService.disableThreshold(id));
    }

    @PostMapping("/{id}/recipients/{userId}")
    public ResponseEntity<BudgetThresholdDto> addNotificationRecipient(
            @PathVariable UUID id,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(thresholdService.addNotificationRecipient(id, userId));
    }

    @DeleteMapping("/{id}/recipients/{userId}")
    public ResponseEntity<BudgetThresholdDto> removeNotificationRecipient(
            @PathVariable UUID id,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(thresholdService.removeNotificationRecipient(id, userId));
    }

    @GetMapping("/{id}/breached")
    public ResponseEntity<Boolean> checkThresholdBreached(@PathVariable UUID id) {
        return ResponseEntity.ok(thresholdService.checkThresholdBreached(id));
    }
}
