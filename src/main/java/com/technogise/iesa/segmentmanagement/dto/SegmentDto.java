package com.technogise.iesa.segmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Segment entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SegmentDto {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private String segmentType;
    private UUID parentSegmentId;
    private String parentSegmentName;
    private String fullPath;
    private Boolean isActive;
    private Integer displayOrder;
    private Map<String, Object> metadata;
    private Boolean isRoot;
    private Boolean isLeaf;
    private Instant createdAt;
    private Instant updatedAt;
}
