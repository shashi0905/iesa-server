package com.technogise.iesa.reporting.controller;

import com.technogise.iesa.reporting.dto.CreateDashboardWidgetRequest;
import com.technogise.iesa.reporting.dto.DashboardWidgetDto;
import com.technogise.iesa.reporting.service.DashboardWidgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard-widgets")
@RequiredArgsConstructor
public class DashboardWidgetController {

    private final DashboardWidgetService widgetService;

    @GetMapping
    public ResponseEntity<List<DashboardWidgetDto>> getAllWidgets() {
        return ResponseEntity.ok(widgetService.getAllWidgets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DashboardWidgetDto> getWidgetById(@PathVariable UUID id) {
        return ResponseEntity.ok(widgetService.getWidgetById(id));
    }

    @GetMapping("/dashboard/{dashboardId}")
    public ResponseEntity<List<DashboardWidgetDto>> getWidgetsByDashboard(@PathVariable UUID dashboardId) {
        return ResponseEntity.ok(widgetService.getWidgetsByDashboard(dashboardId));
    }

    @PostMapping("/dashboard/{dashboardId}")
    public ResponseEntity<DashboardWidgetDto> createWidget(
            @PathVariable UUID dashboardId,
            @Valid @RequestBody CreateDashboardWidgetRequest request) {
        DashboardWidgetDto createdWidget = widgetService.createWidget(dashboardId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWidget);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DashboardWidgetDto> updateWidget(
            @PathVariable UUID id,
            @Valid @RequestBody CreateDashboardWidgetRequest request) {
        return ResponseEntity.ok(widgetService.updateWidget(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWidget(@PathVariable UUID id) {
        widgetService.deleteWidget(id);
        return ResponseEntity.noContent().build();
    }
}
