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

    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.segment LEFT JOIN FETCH b.department WHERE b.segment.id = :segmentId")
    List<Budget> findBySegmentId(@Param("segmentId") UUID segmentId);

    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.segment LEFT JOIN FETCH b.department WHERE b.department.id = :departmentId")
    List<Budget> findByDepartmentId(@Param("departmentId") UUID departmentId);

    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.segment LEFT JOIN FETCH b.department WHERE b.period = :period")
    List<Budget> findByPeriod(@Param("period") BudgetPeriod period);

    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.segment LEFT JOIN FETCH b.department WHERE b.isActive = :isActive")
    List<Budget> findByIsActive(@Param("isActive") Boolean isActive);

    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.segment LEFT JOIN FETCH b.department WHERE b.startDate >= :startDate AND b.startDate <= :endDate AND b.deletedAt IS NULL")
    List<Budget> findByStartDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.segment LEFT JOIN FETCH b.department WHERE b.endDate >= :startDate AND b.endDate <= :endDate AND b.deletedAt IS NULL")
    List<Budget> findByEndDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Budget b WHERE b.name = :name AND b.period = :period AND b.startDate = :startDate AND b.endDate = :endDate AND b.deletedAt IS NULL")
    boolean existsByNameAndPeriod(
        @Param("name") String name,
        @Param("period") BudgetPeriod period,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.segment LEFT JOIN FETCH b.department WHERE b.deletedAt IS NULL")
    List<Budget> findAllNotDeleted();

    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.segment LEFT JOIN FETCH b.department WHERE b.isActive = true AND b.deletedAt IS NULL")
    List<Budget> findAllActive();

    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.segment LEFT JOIN FETCH b.department WHERE :date >= b.startDate AND :date <= b.endDate AND b.deletedAt IS NULL")
    List<Budget> findCurrentBudgets(@Param("date") LocalDate date);

    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.segment LEFT JOIN FETCH b.department WHERE b.segment.id = :segmentId AND :date >= b.startDate AND :date <= b.endDate AND b.isActive = true AND b.deletedAt IS NULL")
    List<Budget> findActiveBySegmentAndDate(@Param("segmentId") UUID segmentId, @Param("date") LocalDate date);

    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.segment LEFT JOIN FETCH b.department WHERE b.department.id = :departmentId AND :date >= b.startDate AND :date <= b.endDate AND b.isActive = true AND b.deletedAt IS NULL")
    List<Budget> findActiveByDepartmentAndDate(@Param("departmentId") UUID departmentId, @Param("date") LocalDate date);
}
