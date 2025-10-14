package com.technogise.iesa.segmentmanagement.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for updating an existing segment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSegmentRequest {

    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private String segmentType;

    private UUID parentSegmentId;

    private Boolean isActive;

    private Integer displayOrder;

    private Map<String, Object> metadata;
}
