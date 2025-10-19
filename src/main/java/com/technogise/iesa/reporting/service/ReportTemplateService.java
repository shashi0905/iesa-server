package com.technogise.iesa.reporting.service;

import com.technogise.iesa.reporting.domain.ReportTemplate;
import com.technogise.iesa.reporting.domain.ReportType;
import com.technogise.iesa.reporting.dto.CreateReportTemplateRequest;
import com.technogise.iesa.reporting.dto.ReportTemplateDto;
import com.technogise.iesa.reporting.dto.ReportTemplateMapper;
import com.technogise.iesa.reporting.dto.UpdateReportTemplateRequest;
import com.technogise.iesa.reporting.repository.ReportTemplateRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportTemplateService {

    private final ReportTemplateRepository reportTemplateRepository;
    private final ReportTemplateMapper reportTemplateMapper;

    @Transactional(readOnly = true)
    public List<ReportTemplateDto> getAllTemplates() {
        return reportTemplateMapper.toDtoList(reportTemplateRepository.findAllNotDeleted());
    }

    @Transactional(readOnly = true)
    public ReportTemplateDto getTemplateById(UUID id) {
        ReportTemplate template = findTemplateById(id);
        return reportTemplateMapper.toDto(template);
    }

    @Transactional(readOnly = true)
    public List<ReportTemplateDto> getTemplatesByType(ReportType reportType) {
        return reportTemplateMapper.toDtoList(reportTemplateRepository.findByReportType(reportType));
    }

    @Transactional(readOnly = true)
    public List<ReportTemplateDto> getSystemTemplates() {
        return reportTemplateMapper.toDtoList(reportTemplateRepository.findByIsSystemTemplate(true));
    }

    @Transactional(readOnly = true)
    public List<ReportTemplateDto> getActiveTemplates() {
        return reportTemplateMapper.toDtoList(reportTemplateRepository.findByIsActive(true));
    }

    public ReportTemplateDto createTemplate(CreateReportTemplateRequest request) {
        ReportTemplate template = ReportTemplate.builder()
                .name(request.getName())
                .description(request.getDescription())
                .reportType(request.getReportType())
                .visualizationType(request.getVisualizationType())
                .queryDefinition(request.getQueryDefinition())
                .configuration(request.getConfiguration())
                .isSystemTemplate(false)
                .isActive(true)
                .build();

        ReportTemplate savedTemplate = reportTemplateRepository.save(template);
        return reportTemplateMapper.toDto(savedTemplate);
    }

    public ReportTemplateDto updateTemplate(UUID id, UpdateReportTemplateRequest request) {
        ReportTemplate template = findTemplateById(id);

        if (request.getName() != null) {
            template.setName(request.getName());
        }
        if (request.getDescription() != null) {
            template.setDescription(request.getDescription());
        }
        if (request.getReportType() != null) {
            template.setReportType(request.getReportType());
        }
        if (request.getVisualizationType() != null) {
            template.setVisualizationType(request.getVisualizationType());
        }
        if (request.getQueryDefinition() != null) {
            template.setQueryDefinition(request.getQueryDefinition());
        }
        if (request.getConfiguration() != null) {
            template.setConfiguration(request.getConfiguration());
        }

        ReportTemplate updatedTemplate = reportTemplateRepository.save(template);
        return reportTemplateMapper.toDto(updatedTemplate);
    }

    public void deleteTemplate(UUID id) {
        ReportTemplate template = findTemplateById(id);
        if (template.getIsSystemTemplate()) {
            throw new IllegalArgumentException("Cannot delete system template");
        }
        template.setDeletedAt(Instant.now());
        reportTemplateRepository.save(template);
    }

    public ReportTemplateDto activateTemplate(UUID id) {
        ReportTemplate template = findTemplateById(id);
        template.setIsActive(true);
        ReportTemplate updatedTemplate = reportTemplateRepository.save(template);
        return reportTemplateMapper.toDto(updatedTemplate);
    }

    public ReportTemplateDto deactivateTemplate(UUID id) {
        ReportTemplate template = findTemplateById(id);
        template.setIsActive(false);
        ReportTemplate updatedTemplate = reportTemplateRepository.save(template);
        return reportTemplateMapper.toDto(updatedTemplate);
    }

    private ReportTemplate findTemplateById(UUID id) {
        return reportTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report template not found with id: " + id));
    }
}
