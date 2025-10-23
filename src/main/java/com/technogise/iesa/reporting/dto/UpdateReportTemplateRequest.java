package com.technogise.iesa.reporting.dto;

import com.technogise.iesa.reporting.domain.ReportType;
import com.technogise.iesa.reporting.domain.VisualizationType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportTemplateRequest {

    @Size(max = 200, message = "Template name must not exceed 200 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private ReportType reportType;

    private VisualizationType visualizationType;

    private String queryDefinition;

    private Map<String, Object> configuration;
}
