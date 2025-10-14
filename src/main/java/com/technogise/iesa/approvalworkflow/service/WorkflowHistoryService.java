package com.technogise.iesa.approvalworkflow.service;

import com.technogise.iesa.approvalworkflow.domain.WorkflowHistory;
import com.technogise.iesa.approvalworkflow.dto.ApprovalWorkflowMapper;
import com.technogise.iesa.approvalworkflow.dto.WorkflowHistoryDto;
import com.technogise.iesa.approvalworkflow.repository.WorkflowHistoryRepository;
import com.technogise.iesa.expensemanagement.domain.Expense;
import com.technogise.iesa.expensemanagement.domain.ExpenseStatus;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import com.technogise.iesa.usermanagement.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WorkflowHistoryService {

    private final WorkflowHistoryRepository historyRepository;
    private final ApprovalWorkflowMapper mapper;

    @Transactional(readOnly = true)
    public List<WorkflowHistoryDto> getHistoryForExpense(UUID expenseId) {
        return mapper.toHistoryDtoList(historyRepository.findByExpenseIdOrderByTimestampDesc(expenseId));
    }

    @Transactional(readOnly = true)
    public List<WorkflowHistoryDto> getHistoryByActor(UUID actorId) {
        return mapper.toHistoryDtoList(historyRepository.findByActorId(actorId));
    }

    @Transactional(readOnly = true)
    public WorkflowHistoryDto getLatestHistoryForExpense(UUID expenseId) {
        WorkflowHistory history = historyRepository.findLatestByExpenseId(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("No history found for expense: " + expenseId));
        return mapper.toDto(history);
    }

    @Transactional(readOnly = true)
    public List<WorkflowHistoryDto> getApprovalTransitions() {
        return mapper.toHistoryDtoList(historyRepository.findApprovalTransitions());
    }

    @Transactional(readOnly = true)
    public List<WorkflowHistoryDto> getRejectionTransitions() {
        return mapper.toHistoryDtoList(historyRepository.findRejectionTransitions());
    }

    /**
     * Create a workflow history entry for status transition
     */
    public WorkflowHistoryDto createHistoryEntry(Expense expense, ExpenseStatus fromStatus,
                                                  ExpenseStatus toStatus, User actor, String comment) {
        log.info("Creating workflow history for expense {} from {} to {}",
                 expense.getId(), fromStatus, toStatus);

        WorkflowHistory history = WorkflowHistory.builder()
                .expense(expense)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .actor(actor)
                .comment(comment)
                .timestamp(Instant.now())
                .build();

        history = historyRepository.save(history);
        return mapper.toDto(history);
    }
}
