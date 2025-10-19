package com.technogise.iesa.reporting.repository;

import com.technogise.iesa.reporting.domain.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {

    @Query("SELECT d FROM Dashboard d LEFT JOIN FETCH d.widgets w LEFT JOIN FETCH w.report WHERE d.owner.id = :ownerId AND d.deletedAt IS NULL")
    List<Dashboard> findAllByOwner(@Param("ownerId") UUID ownerId);

    @Query("SELECT d FROM Dashboard d LEFT JOIN FETCH d.widgets w LEFT JOIN FETCH w.report WHERE d.owner.id = :ownerId AND d.isDefault = true AND d.deletedAt IS NULL")
    Optional<Dashboard> findDefaultByOwner(@Param("ownerId") UUID ownerId);

    @Query("SELECT d FROM Dashboard d LEFT JOIN FETCH d.widgets w LEFT JOIN FETCH w.report WHERE d.isShared = true AND d.deletedAt IS NULL")
    List<Dashboard> findSharedDashboards();

    @Query("SELECT d FROM Dashboard d LEFT JOIN FETCH d.widgets WHERE d.deletedAt IS NULL")
    List<Dashboard> findAllNotDeleted();
}
