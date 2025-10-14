package com.technogise.iesa.segmentmanagement.domain;

import com.technogise.iesa.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Segment entity representing custom categories/dimensions for expense allocation
 * Supports hierarchical structure (parent-child relationships)
 */
@Entity
@Table(name = "segments", indexes = {
    @Index(name = "idx_segment_code", columnList = "code"),
    @Index(name = "idx_segment_name", columnList = "name"),
    @Index(name = "idx_segment_type", columnList = "segment_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Segment extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "segment_type", nullable = false, length = 50)
    private SegmentType segmentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_segment_id")
    private Segment parentSegment;

    @OneToMany(mappedBy = "parentSegment", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private Set<Segment> childSegments = new HashSet<>();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * Get full hierarchical path (e.g., "Engineering > Backend > Microservices")
     */
    @Transient
    public String getFullPath() {
        if (parentSegment != null) {
            return parentSegment.getFullPath() + " > " + name;
        }
        return name;
    }

    /**
     * Check if this segment is a root segment (has no parent)
     */
    @Transient
    public boolean isRoot() {
        return parentSegment == null;
    }

    /**
     * Check if this segment is a leaf segment (has no children)
     */
    @Transient
    public boolean isLeaf() {
        return childSegments.isEmpty();
    }
}
