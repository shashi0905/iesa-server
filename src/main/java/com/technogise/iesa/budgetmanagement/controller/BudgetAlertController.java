package com.technogise.iesa.budgetmanagement.controller;

import com.technogise.iesa.budgetmanagement.dto.BudgetAlertDto;
import com.technogise.iesa.budgetmanagement.service.BudgetAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/budget-alerts")
@RequiredArgsConstructor
public class BudgetAlertController {

    private final BudgetAlertService alertService;

    @GetMapping
    public ResponseEntity<List<BudgetAlertDto>> getAllAlerts() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetAlertDto> getAlertById(@PathVariable UUID id) {
        return ResponseEntity.ok(alertService.getAlertById(id));
    }

    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<BudgetAlertDto>> getAlertsByBudget(@PathVariable UUID budgetId) {
        return ResponseEntity.ok(alertService.getAlertsByBudget(budgetId));
    }

    @GetMapping("/unacknowledged")
    public ResponseEntity<List<BudgetAlertDto>> getUnacknowledgedAlerts() {
        return ResponseEntity.ok(alertService.getUnacknowledgedAlerts());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<BudgetAlertDto>> getRecentAlerts(@RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(alertService.getRecentAlerts(days));
    }

    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<BudgetAlertDto> acknowledgeAlert(@PathVariable UUID id) {
        return ResponseEntity.ok(alertService.acknowledgeAlert(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable UUID id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/acknowledged")
    public ResponseEntity<Integer> deleteAcknowledgedAlerts() {
        int deletedCount = alertService.deleteAcknowledgedAlerts();
        return ResponseEntity.ok(deletedCount);
    }

    @DeleteMapping("/old")
    public ResponseEntity<Integer> deleteOldAlerts(@RequestParam(defaultValue = "30") int days) {
        int deletedCount = alertService.deleteOldAlerts(days);
        return ResponseEntity.ok(deletedCount);
    }

    @PostMapping("/check-and-create")
    public ResponseEntity<Integer> checkAndCreateAlerts() {
        int alertsCreated = alertService.checkAndCreateAlerts();
        return ResponseEntity.ok(alertsCreated);
    }
}
