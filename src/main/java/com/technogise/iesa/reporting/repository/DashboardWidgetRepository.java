package com.technogise.iesa.reporting.repository;

import com.technogise.iesa.reporting.domain.DashboardWidget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DashboardWidgetRepository extends JpaRepository<DashboardWidget, UUID> {

    @Query("SELECT w FROM DashboardWidget w LEFT JOIN FETCH w.report WHERE w.dashboard.id = :dashboardId AND w.deletedAt IS NULL")
    List<DashboardWidget> findByDashboard(@Param("dashboardId") UUID dashboardId);

    @Query("SELECT w FROM DashboardWidget w WHERE w.deletedAt IS NULL")
    List<DashboardWidget> findAllNotDeleted();
}
