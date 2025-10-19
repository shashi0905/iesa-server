package com.technogise.iesa.reporting.dto;

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
public class DashboardWidgetDto {
    private UUID id;
    private UUID dashboardId;
    private UUID reportId;
    private String reportName;
    private String title;
    private Map<String, Object> position;
    private Integer refreshInterval;
    private Integer width;
    private Integer height;
    private Integer orderIndex;
    private Instant createdAt;
    private Instant updatedAt;
}
