package com.technogise.iesa.reporting.repository;

import com.technogise.iesa.reporting.domain.ReportTemplate;
import com.technogise.iesa.reporting.domain.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, UUID> {

    @Query("SELECT rt FROM ReportTemplate rt WHERE rt.deletedAt IS NULL")
    List<ReportTemplate> findAllNotDeleted();

    @Query("SELECT rt FROM ReportTemplate rt WHERE rt.reportType = :reportType AND rt.deletedAt IS NULL")
    List<ReportTemplate> findByReportType(@Param("reportType") ReportType reportType);

    @Query("SELECT rt FROM ReportTemplate rt WHERE rt.isSystemTemplate = :isSystemTemplate AND rt.deletedAt IS NULL")
    List<ReportTemplate> findByIsSystemTemplate(@Param("isSystemTemplate") Boolean isSystemTemplate);

    @Query("SELECT rt FROM ReportTemplate rt WHERE rt.isActive = :isActive AND rt.deletedAt IS NULL")
    List<ReportTemplate> findByIsActive(@Param("isActive") Boolean isActive);
}
