package com.technogise.iesa.reporting.domain;

import com.technogise.iesa.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * Entity representing a widget in a dashboard
 */
@Entity
@Table(name = "dashboard_widgets", indexes = {
    @Index(name = "idx_widget_dashboard", columnList = "dashboard_id"),
    @Index(name = "idx_widget_report", columnList = "report_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DashboardWidget extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dashboard_id", nullable = false)
    private Dashboard dashboard;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(name = "title", length = 200)
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "position", columnDefinition = "jsonb")
    private Map<String, Object> position;

    @Column(name = "refresh_interval")
    private Integer refreshInterval; // in seconds

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "order_index")
    private Integer orderIndex;
}
