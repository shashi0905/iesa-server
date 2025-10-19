package com.technogise.iesa.reporting.repository;

import com.technogise.iesa.reporting.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.template LEFT JOIN FETCH r.createdByUser WHERE r.createdByUser.id = :userId AND r.deletedAt IS NULL")
    List<Report> findAllByCreatedByUser(@Param("userId") UUID userId);

    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.template LEFT JOIN FETCH r.createdByUser WHERE r.template.id = :templateId AND r.deletedAt IS NULL")
    List<Report> findByTemplateId(@Param("templateId") UUID templateId);

    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.template LEFT JOIN FETCH r.createdByUser WHERE r.createdByUser.id = :userId AND r.isFavorite = true AND r.deletedAt IS NULL")
    List<Report> findFavoritesByUser(@Param("userId") UUID userId);

    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.template LEFT JOIN FETCH r.createdByUser WHERE r.deletedAt IS NULL")
    List<Report> findAllNotDeleted();
}
