package com.technogise.iesa.reporting.service;

import com.technogise.iesa.reporting.domain.Report;
import com.technogise.iesa.reporting.domain.ReportTemplate;
import com.technogise.iesa.reporting.dto.*;
import com.technogise.iesa.reporting.repository.ReportRepository;
import com.technogise.iesa.reporting.repository.ReportTemplateRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import com.technogise.iesa.usermanagement.domain.User;
import com.technogise.iesa.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportTemplateRepository reportTemplateRepository;
    private final UserRepository userRepository;
    private final ReportMapper reportMapper;

    @Transactional(readOnly = true)
    public List<ReportDto> getAllReports() {
        return reportMapper.toDtoList(reportRepository.findAllNotDeleted());
    }

    @Transactional(readOnly = true)
    public ReportDto getReportById(UUID id) {
        Report report = findReportById(id);
        return reportMapper.toDto(report);
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getReportsByUser(UUID userId) {
        return reportMapper.toDtoList(reportRepository.findAllByCreatedByUser(userId));
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getReportsByTemplate(UUID templateId) {
        return reportMapper.toDtoList(reportRepository.findByTemplateId(templateId));
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getFavoriteReports(UUID userId) {
        return reportMapper.toDtoList(reportRepository.findFavoritesByUser(userId));
    }

    public ReportDto createReport(CreateReportRequest request, UUID userId) {
        ReportTemplate template = reportTemplateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new ResourceNotFoundException("Report template not found with id: " + request.getTemplateId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate date range if provided
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getEndDate().isBefore(request.getStartDate())) {
                throw new IllegalArgumentException("End date must be after start date");
            }
        }

        Report report = Report.builder()
                .name(request.getName())
                .description(request.getDescription())
                .template(template)
                .createdByUser(user)
                .filters(request.getFilters())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .scheduledCron(request.getScheduledCron())
                .isFavorite(false)
                .executionCount(0)
                .build();

        Report savedReport = reportRepository.save(report);
        return reportMapper.toDto(savedReport);
    }

    public ReportDto updateReport(UUID id, UpdateReportRequest request) {
        Report report = findReportById(id);

        if (request.getName() != null) {
            report.setName(request.getName());
        }
        if (request.getDescription() != null) {
            report.setDescription(request.getDescription());
        }
        if (request.getTemplateId() != null) {
            ReportTemplate template = reportTemplateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException("Report template not found with id: " + request.getTemplateId()));
            report.setTemplate(template);
        }
        if (request.getFilters() != null) {
            report.setFilters(request.getFilters());
        }
        if (request.getStartDate() != null) {
            report.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            report.setEndDate(request.getEndDate());
        }
        if (request.getScheduledCron() != null) {
            report.setScheduledCron(request.getScheduledCron());
        }

        // Validate date range if both dates are set
        if (report.getStartDate() != null && report.getEndDate() != null) {
            if (report.getEndDate().isBefore(report.getStartDate())) {
                throw new IllegalArgumentException("End date must be after start date");
            }
        }

        Report updatedReport = reportRepository.save(report);
        return reportMapper.toDto(updatedReport);
    }

    public void deleteReport(UUID id) {
        Report report = findReportById(id);
        report.setDeletedAt(Instant.now());
        reportRepository.save(report);
    }

    public ReportDto toggleFavorite(UUID id) {
        Report report = findReportById(id);
        report.setIsFavorite(!report.getIsFavorite());
        Report updatedReport = reportRepository.save(report);
        return reportMapper.toDto(updatedReport);
    }

    public ReportExecutionResult executeReport(UUID id) {
        Report report = findReportById(id);

        // Update execution tracking
        report.setLastExecutedAt(Instant.now());
        report.setExecutionCount(report.getExecutionCount() + 1);
        reportRepository.save(report);

        // TODO: Implement actual report execution logic based on template query definition
        // This is a placeholder implementation
        long startTime = System.currentTimeMillis();

        List<HashMap<String, Object>> data = new ArrayList<>();
        // Add sample data structure
        HashMap<String, Object> row = new HashMap<>();
        row.put("message", "Report execution not yet implemented");
        data.add(row);

        long executionTime = System.currentTimeMillis() - startTime;

        return ReportExecutionResult.builder()
                .data(new ArrayList<>(data))
                .totalRecords(data.size())
                .executionTime(executionTime)
                .build();
    }

    private Report findReportById(UUID id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));
    }
}
