package com.technogise.iesa.approvalworkflow.controller;

import com.technogise.iesa.approvalworkflow.dto.CommentDto;
import com.technogise.iesa.approvalworkflow.dto.CreateCommentRequest;
import com.technogise.iesa.approvalworkflow.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/expense/{expenseId}")
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<List<CommentDto>> getCommentsForExpense(@PathVariable UUID expenseId) {
        return ResponseEntity.ok(commentService.getCommentsForExpense(expenseId));
    }

    @GetMapping("/expense/{expenseId}/external")
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<List<CommentDto>> getExternalCommentsForExpense(@PathVariable UUID expenseId) {
        return ResponseEntity.ok(commentService.getExternalCommentsForExpense(expenseId));
    }

    @GetMapping("/expense/{expenseId}/internal")
    @PreAuthorize("hasAnyAuthority('EXPENSE_APPROVE', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<CommentDto>> getInternalCommentsForExpense(@PathVariable UUID expenseId) {
        return ResponseEntity.ok(commentService.getInternalCommentsForExpense(expenseId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable UUID id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('EXPENSE_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EXPENSE_UPDATE', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
