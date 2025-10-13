package com.technogise.iesa.segmentmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for creating a new segment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSegmentRequest {

    @NotBlank(message = "Segment name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    @NotBlank(message = "Segment code is required")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    private String code;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotBlank(message = "Segment type is required")
    private String segmentType;

    private UUID parentSegmentId;

    private Integer displayOrder;

    private Map<String, Object> metadata;
}
