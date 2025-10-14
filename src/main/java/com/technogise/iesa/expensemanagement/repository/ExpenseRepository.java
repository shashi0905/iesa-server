package com.technogise.iesa.expensemanagement.repository;

import com.technogise.iesa.expensemanagement.domain.Expense;
import com.technogise.iesa.expensemanagement.domain.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Expense entity
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    /**
     * Find all expenses by submitter (excluding soft-deleted)
     */
    @Query("SELECT e FROM Expense e WHERE e.submitter.id = :submitterId AND e.deletedAt IS NULL ORDER BY e.expenseDate DESC")
    List<Expense> findBySubmitterId(@Param("submitterId") UUID submitterId);

    /**
     * Find all expenses by status (excluding soft-deleted)
     */
    @Query("SELECT e FROM Expense e WHERE e.status = :status AND e.deletedAt IS NULL ORDER BY e.expenseDate DESC")
    List<Expense> findByStatus(@Param("status") ExpenseStatus status);

    /**
     * Find all expenses by submitter and status (excluding soft-deleted)
     */
    @Query("SELECT e FROM Expense e WHERE e.submitter.id = :submitterId AND e.status = :status AND e.deletedAt IS NULL ORDER BY e.expenseDate DESC")
    List<Expense> findBySubmitterIdAndStatus(
        @Param("submitterId") UUID submitterId,
        @Param("status") ExpenseStatus status
    );

    /**
     * Find all expenses within date range (excluding soft-deleted)
     */
    @Query("SELECT e FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate AND e.deletedAt IS NULL ORDER BY e.expenseDate DESC")
    List<Expense> findByExpenseDateBetween(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find all expenses for a user's department (excluding soft-deleted)
     */
    @Query("SELECT e FROM Expense e WHERE e.submitter.department.id = :departmentId AND e.deletedAt IS NULL ORDER BY e.expenseDate DESC")
    List<Expense> findByDepartmentId(@Param("departmentId") UUID departmentId);

    /**
     * Find all pending approval expenses (excluding soft-deleted)
     */
    @Query("SELECT e FROM Expense e WHERE e.status = 'SUBMITTED' AND e.deletedAt IS NULL ORDER BY e.submissionDate ASC")
    List<Expense> findPendingApprovals();

    /**
     * Find all expenses (excluding soft-deleted)
     */
    @Query("SELECT e FROM Expense e WHERE e.deletedAt IS NULL ORDER BY e.expenseDate DESC")
    List<Expense> findAllNotDeleted();

    /**
     * Search expenses by vendor or description (excluding soft-deleted)
     */
    @Query("SELECT e FROM Expense e WHERE (LOWER(e.vendor) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND e.deletedAt IS NULL ORDER BY e.expenseDate DESC")
    List<Expense> searchExpenses(@Param("searchTerm") String searchTerm);

    /**
     * Count expenses by status (excluding soft-deleted)
     */
    @Query("SELECT COUNT(e) FROM Expense e WHERE e.status = :status AND e.deletedAt IS NULL")
    long countByStatus(@Param("status") ExpenseStatus status);

    /**
     * Sum total amounts by status (excluding soft-deleted)
     */
    @Query("SELECT COALESCE(SUM(e.totalAmount), 0) FROM Expense e WHERE e.status = :status AND e.deletedAt IS NULL")
    java.math.BigDecimal sumTotalAmountByStatus(@Param("status") ExpenseStatus status);
}
