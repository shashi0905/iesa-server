package com.technogise.iesa.budgetmanagement.repository;

import com.technogise.iesa.budgetmanagement.domain.Budget;
import com.technogise.iesa.budgetmanagement.domain.BudgetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    List<Budget> findBySegmentId(UUID segmentId);

    List<Budget> findByDepartmentId(UUID departmentId);

    List<Budget> findByPeriod(BudgetPeriod period);

    List<Budget> findByIsActive(Boolean isActive);

    @Query("SELECT b FROM Budget b WHERE b.startDate >= :startDate AND b.startDate <= :endDate AND b.deletedAt IS NULL")
    List<Budget> findByStartDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM Budget b WHERE b.endDate >= :startDate AND b.endDate <= :endDate AND b.deletedAt IS NULL")
    List<Budget> findByEndDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Budget b WHERE b.name = :name AND b.period = :period AND b.startDate = :startDate AND b.endDate = :endDate AND b.deletedAt IS NULL")
    boolean existsByNameAndPeriod(
        @Param("name") String name,
        @Param("period") BudgetPeriod period,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT b FROM Budget b WHERE b.deletedAt IS NULL")
    List<Budget> findAllNotDeleted();

    @Query("SELECT b FROM Budget b WHERE b.isActive = true AND b.deletedAt IS NULL")
    List<Budget> findAllActive();

    @Query("SELECT b FROM Budget b WHERE :date >= b.startDate AND :date <= b.endDate AND b.deletedAt IS NULL")
    List<Budget> findCurrentBudgets(@Param("date") LocalDate date);

    @Query("SELECT b FROM Budget b WHERE b.segment.id = :segmentId AND :date >= b.startDate AND :date <= b.endDate AND b.isActive = true AND b.deletedAt IS NULL")
    List<Budget> findActiveBySegmentAndDate(@Param("segmentId") UUID segmentId, @Param("date") LocalDate date);

    @Query("SELECT b FROM Budget b WHERE b.department.id = :departmentId AND :date >= b.startDate AND :date <= b.endDate AND b.isActive = true AND b.deletedAt IS NULL")
    List<Budget> findActiveByDepartmentAndDate(@Param("departmentId") UUID departmentId, @Param("date") LocalDate date);
}
