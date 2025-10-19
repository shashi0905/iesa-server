package com.technogise.iesa.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;
    private String ownerName;
    @Builder.Default
    private List<DashboardWidgetDto> widgets = new ArrayList<>();
    private Map<String, Object> layout;
    private Boolean isDefault;
    private Boolean isShared;
    private Instant createdAt;
    private Instant updatedAt;
}
