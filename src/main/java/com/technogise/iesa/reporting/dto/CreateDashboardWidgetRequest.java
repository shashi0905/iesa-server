package com.technogise.iesa.reporting.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDashboardWidgetRequest {

    @NotNull(message = "Report ID is required")
    private UUID reportId;

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    private Map<String, Object> position;

    private Integer refreshInterval;

    private Integer width;

    private Integer height;

    private Integer orderIndex;
}
