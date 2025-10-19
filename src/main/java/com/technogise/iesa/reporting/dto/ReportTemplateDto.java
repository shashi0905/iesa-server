package com.technogise.iesa.reporting.dto;

import com.technogise.iesa.reporting.domain.ReportType;
import com.technogise.iesa.reporting.domain.VisualizationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportTemplateDto {
    private UUID id;
    private String name;
    private String description;
    private ReportType reportType;
    private VisualizationType visualizationType;
    private String queryDefinition;
    private Map<String, Object> configuration;
    private Boolean isSystemTemplate;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
