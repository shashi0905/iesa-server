package com.technogise.iesa.reporting.domain;

import com.technogise.iesa.shared.domain.BaseEntity;
import com.technogise.iesa.usermanagement.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.Map;

/**
 * Entity representing a custom report
 */
@Entity
@Table(name = "reports", indexes = {
    @Index(name = "idx_report_created_by", columnList = "created_by_user_id"),
    @Index(name = "idx_report_template", columnList = "template_id"),
    @Index(name = "idx_report_date_range", columnList = "start_date, end_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Report extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id", nullable = false)
    private ReportTemplate template;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "filters", columnDefinition = "jsonb")
    private Map<String, Object> filters;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "scheduled_cron")
    private String scheduledCron;

    @Column(name = "is_favorite", nullable = false)
    @Builder.Default
    private Boolean isFavorite = false;

    @Column(name = "last_executed_at")
    private java.time.Instant lastExecutedAt;

    @Column(name = "execution_count")
    @Builder.Default
    private Integer executionCount = 0;
}
