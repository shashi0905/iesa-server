package com.technogise.iesa.reporting.controller;

import com.technogise.iesa.reporting.dto.CreateReportRequest;
import com.technogise.iesa.reporting.dto.ReportDto;
import com.technogise.iesa.reporting.dto.ReportExecutionResult;
import com.technogise.iesa.reporting.dto.UpdateReportRequest;
import com.technogise.iesa.reporting.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<List<ReportDto>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportDto> getReportById(@PathVariable UUID id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReportDto>> getReportsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(reportService.getReportsByUser(userId));
    }

    @GetMapping("/template/{templateId}")
    public ResponseEntity<List<ReportDto>> getReportsByTemplate(@PathVariable UUID templateId) {
        return ResponseEntity.ok(reportService.getReportsByTemplate(templateId));
    }

    @GetMapping("/favorites/{userId}")
    public ResponseEntity<List<ReportDto>> getFavoriteReports(@PathVariable UUID userId) {
        return ResponseEntity.ok(reportService.getFavoriteReports(userId));
    }

    @PostMapping
    public ResponseEntity<ReportDto> createReport(
            @Valid @RequestBody CreateReportRequest request,
            @RequestParam UUID userId) {
        ReportDto createdReport = reportService.createReport(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReport);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportDto> updateReport(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReportRequest request) {
        return ResponseEntity.ok(reportService.updateReport(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable UUID id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/favorite")
    public ResponseEntity<ReportDto> toggleFavorite(@PathVariable UUID id) {
        return ResponseEntity.ok(reportService.toggleFavorite(id));
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<ReportExecutionResult> executeReport(@PathVariable UUID id) {
        return ResponseEntity.ok(reportService.executeReport(id));
    }
}
