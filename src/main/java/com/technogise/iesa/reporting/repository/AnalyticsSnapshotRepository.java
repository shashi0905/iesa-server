package com.technogise.iesa.reporting.repository;

import com.technogise.iesa.reporting.domain.AnalyticsSnapshot;
import com.technogise.iesa.reporting.domain.DimensionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalyticsSnapshotRepository extends JpaRepository<AnalyticsSnapshot, UUID> {

    @Query("SELECT a FROM AnalyticsSnapshot a WHERE a.snapshotDate = :snapshotDate AND a.dimension = :dimension AND a.deletedAt IS NULL")
    List<AnalyticsSnapshot> findBySnapshotDateAndDimension(
            @Param("snapshotDate") LocalDate snapshotDate,
            @Param("dimension") DimensionType dimension);

    @Query("SELECT a FROM AnalyticsSnapshot a WHERE a.dimension = :dimension AND a.snapshotDate >= :startDate AND a.snapshotDate <= :endDate AND a.deletedAt IS NULL ORDER BY a.snapshotDate")
    List<AnalyticsSnapshot> findByDimensionAndDateRange(
            @Param("dimension") DimensionType dimension,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM AnalyticsSnapshot a WHERE a.dimension = :dimension AND a.snapshotDate = (SELECT MAX(a2.snapshotDate) FROM AnalyticsSnapshot a2 WHERE a2.dimension = :dimension AND a2.deletedAt IS NULL) AND a.deletedAt IS NULL")
    List<AnalyticsSnapshot> findLatestByDimension(@Param("dimension") DimensionType dimension);

    @Query("SELECT a FROM AnalyticsSnapshot a WHERE a.snapshotDate = :snapshotDate AND a.dimension = :dimension AND a.dimensionValue = :dimensionValue AND a.deletedAt IS NULL")
    Optional<AnalyticsSnapshot> findBySnapshotDateAndDimensionAndValue(
            @Param("snapshotDate") LocalDate snapshotDate,
            @Param("dimension") DimensionType dimension,
            @Param("dimensionValue") String dimensionValue);
}
