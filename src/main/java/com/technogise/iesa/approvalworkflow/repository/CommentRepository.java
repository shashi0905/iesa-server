package com.technogise.iesa.approvalworkflow.repository;

import com.technogise.iesa.approvalworkflow.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Comment entity
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    /**
     * Find all comments for an expense ordered by creation date
     */
    @Query("SELECT c FROM Comment c WHERE c.expense.id = :expenseId ORDER BY c.createdAt DESC")
    List<Comment> findByExpenseIdOrderByCreatedAtDesc(UUID expenseId);

    /**
     * Find all comments by author
     */
    @Query("SELECT c FROM Comment c WHERE c.author.id = :authorId ORDER BY c.createdAt DESC")
    List<Comment> findByAuthorId(UUID authorId);

    /**
     * Find external (non-internal) comments for an expense
     */
    @Query("SELECT c FROM Comment c WHERE c.expense.id = :expenseId AND c.isInternal = false ORDER BY c.createdAt DESC")
    List<Comment> findExternalCommentsByExpenseId(UUID expenseId);

    /**
     * Find internal comments for an expense
     */
    @Query("SELECT c FROM Comment c WHERE c.expense.id = :expenseId AND c.isInternal = true ORDER BY c.createdAt DESC")
    List<Comment> findInternalCommentsByExpenseId(UUID expenseId);

    /**
     * Count comments for an expense
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.expense.id = :expenseId")
    long countByExpenseId(UUID expenseId);

    /**
     * Count internal comments for an expense
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.expense.id = :expenseId AND c.isInternal = true")
    long countInternalCommentsByExpenseId(UUID expenseId);
}
