package com.technogise.iesa.reporting.controller;

import com.technogise.iesa.reporting.domain.ReportType;
import com.technogise.iesa.reporting.dto.CreateReportTemplateRequest;
import com.technogise.iesa.reporting.dto.ReportTemplateDto;
import com.technogise.iesa.reporting.dto.UpdateReportTemplateRequest;
import com.technogise.iesa.reporting.service.ReportTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/report-templates")
@RequiredArgsConstructor
public class ReportTemplateController {

    private final ReportTemplateService reportTemplateService;

    @GetMapping
    public ResponseEntity<List<ReportTemplateDto>> getAllTemplates() {
        return ResponseEntity.ok(reportTemplateService.getAllTemplates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportTemplateDto> getTemplateById(@PathVariable UUID id) {
        return ResponseEntity.ok(reportTemplateService.getTemplateById(id));
    }

    @GetMapping("/type/{reportType}")
    public ResponseEntity<List<ReportTemplateDto>> getTemplatesByType(@PathVariable ReportType reportType) {
        return ResponseEntity.ok(reportTemplateService.getTemplatesByType(reportType));
    }

    @GetMapping("/system")
    public ResponseEntity<List<ReportTemplateDto>> getSystemTemplates() {
        return ResponseEntity.ok(reportTemplateService.getSystemTemplates());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ReportTemplateDto>> getActiveTemplates() {
        return ResponseEntity.ok(reportTemplateService.getActiveTemplates());
    }

    @PostMapping
    public ResponseEntity<ReportTemplateDto> createTemplate(@Valid @RequestBody CreateReportTemplateRequest request) {
        ReportTemplateDto createdTemplate = reportTemplateService.createTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTemplate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportTemplateDto> updateTemplate(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReportTemplateRequest request) {
        return ResponseEntity.ok(reportTemplateService.updateTemplate(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable UUID id) {
        reportTemplateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ReportTemplateDto> activateTemplate(@PathVariable UUID id) {
        return ResponseEntity.ok(reportTemplateService.activateTemplate(id));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ReportTemplateDto> deactivateTemplate(@PathVariable UUID id) {
        return ResponseEntity.ok(reportTemplateService.deactivateTemplate(id));
    }
}
