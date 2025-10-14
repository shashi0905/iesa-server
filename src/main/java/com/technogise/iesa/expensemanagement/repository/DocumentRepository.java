package com.technogise.iesa.expensemanagement.repository;

import com.technogise.iesa.expensemanagement.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Document entity
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    /**
     * Find all documents for an expense (excluding soft-deleted)
     */
    @Query("SELECT d FROM Document d WHERE d.expense.id = :expenseId AND d.deletedAt IS NULL")
    List<Document> findByExpenseId(@Param("expenseId") UUID expenseId);

    /**
     * Find all documents uploaded by a user (excluding soft-deleted)
     */
    @Query("SELECT d FROM Document d WHERE d.uploadedBy.id = :userId AND d.deletedAt IS NULL ORDER BY d.createdAt DESC")
    List<Document> findByUploadedById(@Param("userId") UUID userId);

    /**
     * Count documents for an expense (excluding soft-deleted)
     */
    @Query("SELECT COUNT(d) FROM Document d WHERE d.expense.id = :expenseId AND d.deletedAt IS NULL")
    long countByExpenseId(@Param("expenseId") UUID expenseId);

    /**
     * Delete all documents for an expense
     */
    @Query("DELETE FROM Document d WHERE d.expense.id = :expenseId")
    void deleteByExpenseId(@Param("expenseId") UUID expenseId);
}
