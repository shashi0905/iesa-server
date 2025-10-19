package com.technogise.iesa.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private UUID id;
    private String name;
    private String description;
    private UUID templateId;
    private String templateName;
    private UUID createdByUserId;
    private String createdByUserName;
    private Map<String, Object> filters;
    private LocalDate startDate;
    private LocalDate endDate;
    private String scheduledCron;
    private Boolean isFavorite;
    private Instant lastExecutedAt;
    private Integer executionCount;
    private Instant createdAt;
    private Instant updatedAt;
}
