package com.technogise.iesa.reporting.domain;

import com.technogise.iesa.shared.domain.BaseEntity;
import com.technogise.iesa.usermanagement.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entity representing a user dashboard
 */
@Entity
@Table(name = "dashboards", indexes = {
    @Index(name = "idx_dashboard_owner", columnList = "owner_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Dashboard extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "dashboard", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DashboardWidget> widgets = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "layout", columnDefinition = "jsonb")
    private Map<String, Object> layout;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "is_shared", nullable = false)
    @Builder.Default
    private Boolean isShared = false;

    /**
     * Add a widget to this dashboard
     */
    public void addWidget(DashboardWidget widget) {
        widgets.add(widget);
        widget.setDashboard(this);
    }

    /**
     * Remove a widget from this dashboard
     */
    public void removeWidget(DashboardWidget widget) {
        widgets.remove(widget);
        widget.setDashboard(null);
    }
}
