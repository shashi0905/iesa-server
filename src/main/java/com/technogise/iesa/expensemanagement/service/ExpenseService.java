package com.technogise.iesa.expensemanagement.service;

import com.technogise.iesa.expensemanagement.domain.*;
import com.technogise.iesa.expensemanagement.dto.*;
import com.technogise.iesa.expensemanagement.repository.*;
import com.technogise.iesa.segmentmanagement.domain.Segment;
import com.technogise.iesa.segmentmanagement.repository.SegmentRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import com.technogise.iesa.usermanagement.domain.User;
import com.technogise.iesa.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final SegmentAllocationRepository segmentAllocationRepository;
    private final UserRepository userRepository;
    private final SegmentRepository segmentRepository;
    private final ExpenseMapper expenseMapper;

    @Transactional(readOnly = true)
    public List<ExpenseDto> getAllExpenses() {
        return expenseMapper.toDtoList(expenseRepository.findAllNotDeleted());
    }

    @Transactional(readOnly = true)
    public ExpenseDto getExpenseById(UUID id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        return expenseMapper.toDto(expense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseDto> getExpensesBySubmitter(UUID submitterId) {
        return expenseMapper.toDtoList(expenseRepository.findBySubmitterId(submitterId));
    }

    @Transactional(readOnly = true)
    public List<ExpenseDto> getExpensesByStatus(ExpenseStatus status) {
        return expenseMapper.toDtoList(expenseRepository.findByStatus(status));
    }

    @Transactional(readOnly = true)
    public List<ExpenseDto> getPendingApprovals() {
        return expenseMapper.toDtoList(expenseRepository.findPendingApprovals());
    }

    public ExpenseDto createExpense(CreateExpenseRequest request) {
        log.info("Creating new expense for date: {}", request.getExpenseDate());

        // Get current user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User submitter = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // Validate segment allocations sum to 100%
        validateSegmentAllocations(request.getSegmentAllocations(), request.getTotalAmount());

        // Create expense
        Expense expense = Expense.builder()
                .submitter(submitter)
                .expenseDate(request.getExpenseDate())
                .vendor(request.getVendor())
                .totalAmount(request.getTotalAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .status(ExpenseStatus.DRAFT)
                .build();

        // Add segment allocations
        for (SegmentAllocationRequest allocReq : request.getSegmentAllocations()) {
            Segment segment = segmentRepository.findById(allocReq.getSegmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Segment not found: " + allocReq.getSegmentId()));

            BigDecimal amount = request.getTotalAmount()
                    .multiply(allocReq.getPercentage())
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);

            SegmentAllocation allocation = SegmentAllocation.builder()
                    .segment(segment)
                    .amount(amount)
                    .percentage(allocReq.getPercentage())
                    .description(allocReq.getDescription())
                    .build();

            expense.addSegmentAllocation(allocation);
        }

        expense = expenseRepository.save(expense);
        log.info("Expense created successfully with id: {}", expense.getId());

        return expenseMapper.toDto(expense);
    }

    public ExpenseDto updateExpense(UUID id, UpdateExpenseRequest request) {
        log.info("Updating expense with id: {}", id);

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (!expense.isEditable()) {
            throw new IllegalStateException("Expense cannot be edited in current status: " + expense.getStatus());
        }

        if (request.getExpenseDate() != null) expense.setExpenseDate(request.getExpenseDate());
        if (request.getVendor() != null) expense.setVendor(request.getVendor());
        if (request.getDescription() != null) expense.setDescription(request.getDescription());

        if (request.getTotalAmount() != null && request.getSegmentAllocations() != null) {
            validateSegmentAllocations(request.getSegmentAllocations(), request.getTotalAmount());
            expense.setTotalAmount(request.getTotalAmount());

            // Remove existing allocations
            expense.getSegmentAllocations().clear();

            // Add new allocations
            for (SegmentAllocationRequest allocReq : request.getSegmentAllocations()) {
                Segment segment = segmentRepository.findById(allocReq.getSegmentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Segment not found: " + allocReq.getSegmentId()));

                BigDecimal amount = request.getTotalAmount()
                        .multiply(allocReq.getPercentage())
                        .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);

                SegmentAllocation allocation = SegmentAllocation.builder()
                        .segment(segment)
                        .amount(amount)
                        .percentage(allocReq.getPercentage())
                        .description(allocReq.getDescription())
                        .build();

                expense.addSegmentAllocation(allocation);
            }
        }

        expense = expenseRepository.save(expense);
        return expenseMapper.toDto(expense);
    }

    public void deleteExpense(UUID id) {
        log.info("Deleting expense with id: {}", id);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (!expense.isEditable()) {
            throw new IllegalStateException("Expense cannot be deleted in current status: " + expense.getStatus());
        }

        expense.setDeletedAt(java.time.Instant.now());
        expenseRepository.save(expense);
    }

    public ExpenseDto submitExpense(UUID id) {
        log.info("Submitting expense with id: {}", id);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (!expense.canBeSubmitted()) {
            throw new IllegalStateException("Expense cannot be submitted");
        }

        expense.setStatus(ExpenseStatus.SUBMITTED);
        expense.setSubmissionDate(LocalDate.now());
        expense = expenseRepository.save(expense);

        return expenseMapper.toDto(expense);
    }

    public ExpenseDto approveExpense(UUID id) {
        log.info("Approving expense with id: {}", id);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (!expense.canBeApproved()) {
            throw new IllegalStateException("Expense cannot be approved in current status");
        }

        expense.setStatus(ExpenseStatus.APPROVED);
        expense.setApprovalDate(LocalDate.now());
        expense = expenseRepository.save(expense);

        return expenseMapper.toDto(expense);
    }

    public ExpenseDto rejectExpense(UUID id, String reason) {
        log.info("Rejecting expense with id: {}", id);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (!expense.canBeRejected()) {
            throw new IllegalStateException("Expense cannot be rejected in current status");
        }

        expense.setStatus(ExpenseStatus.REJECTED);
        expense.setRejectionReason(reason);
        expense = expenseRepository.save(expense);

        return expenseMapper.toDto(expense);
    }

    private void validateSegmentAllocations(List<SegmentAllocationRequest> allocations, BigDecimal totalAmount) {
        if (allocations == null || allocations.isEmpty()) {
            throw new IllegalArgumentException("At least one segment allocation is required");
        }

        BigDecimal totalPercentage = allocations.stream()
                .map(SegmentAllocationRequest::getPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPercentage.compareTo(new BigDecimal("100")) != 0) {
            throw new IllegalArgumentException("Segment allocations must sum to 100%. Current sum: " + totalPercentage);
        }
    }
}
