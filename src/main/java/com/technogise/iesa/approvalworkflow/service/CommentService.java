package com.technogise.iesa.approvalworkflow.service;

import com.technogise.iesa.approvalworkflow.domain.Comment;
import com.technogise.iesa.approvalworkflow.dto.CommentDto;
import com.technogise.iesa.approvalworkflow.dto.CreateCommentRequest;
import com.technogise.iesa.approvalworkflow.dto.ApprovalWorkflowMapper;
import com.technogise.iesa.approvalworkflow.repository.CommentRepository;
import com.technogise.iesa.expensemanagement.domain.Expense;
import com.technogise.iesa.expensemanagement.repository.ExpenseRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import com.technogise.iesa.usermanagement.domain.User;
import com.technogise.iesa.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ApprovalWorkflowMapper mapper;

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsForExpense(UUID expenseId) {
        return mapper.toCommentDtoList(commentRepository.findByExpenseIdOrderByCreatedAtDesc(expenseId));
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getExternalCommentsForExpense(UUID expenseId) {
        return mapper.toCommentDtoList(commentRepository.findExternalCommentsByExpenseId(expenseId));
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getInternalCommentsForExpense(UUID expenseId) {
        return mapper.toCommentDtoList(commentRepository.findInternalCommentsByExpenseId(expenseId));
    }

    @Transactional(readOnly = true)
    public CommentDto getCommentById(UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        return mapper.toDto(comment);
    }

    public CommentDto createComment(CreateCommentRequest request) {
        log.info("Creating new comment for expense: {}", request.getExpenseId());

        // Get current user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // Get expense
        Expense expense = expenseRepository.findById(request.getExpenseId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + request.getExpenseId()));

        // Create comment
        Comment comment = Comment.builder()
                .expense(expense)
                .author(author)
                .content(request.getContent())
                .isInternal(request.getIsInternal() != null ? request.getIsInternal() : false)
                .build();

        comment = commentRepository.save(comment);
        log.info("Comment created successfully with id: {}", comment.getId());

        return mapper.toDto(comment);
    }

    public void deleteComment(UUID id) {
        log.info("Deleting comment with id: {}", id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        commentRepository.delete(comment);
    }
}
