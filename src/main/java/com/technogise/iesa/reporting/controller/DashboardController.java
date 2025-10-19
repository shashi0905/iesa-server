package com.technogise.iesa.reporting.controller;

import com.technogise.iesa.reporting.dto.CreateDashboardRequest;
import com.technogise.iesa.reporting.dto.DashboardDto;
import com.technogise.iesa.reporting.dto.UpdateDashboardRequest;
import com.technogise.iesa.reporting.service.DashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<List<DashboardDto>> getAllDashboards() {
        return ResponseEntity.ok(dashboardService.getAllDashboards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DashboardDto> getDashboardById(@PathVariable UUID id) {
        return ResponseEntity.ok(dashboardService.getDashboardById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<DashboardDto>> getDashboardsByOwner(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(dashboardService.getDashboardsByOwner(ownerId));
    }

    @GetMapping("/owner/{ownerId}/default")
    public ResponseEntity<DashboardDto> getDefaultDashboard(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(dashboardService.getDefaultDashboard(ownerId));
    }

    @GetMapping("/shared")
    public ResponseEntity<List<DashboardDto>> getSharedDashboards() {
        return ResponseEntity.ok(dashboardService.getSharedDashboards());
    }

    @PostMapping
    public ResponseEntity<DashboardDto> createDashboard(
            @Valid @RequestBody CreateDashboardRequest request,
            @RequestParam UUID ownerId) {
        DashboardDto createdDashboard = dashboardService.createDashboard(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDashboard);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DashboardDto> updateDashboard(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDashboardRequest request) {
        return ResponseEntity.ok(dashboardService.updateDashboard(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDashboard(@PathVariable UUID id) {
        dashboardService.deleteDashboard(id);
        return ResponseEntity.noContent().build();
    }
}
