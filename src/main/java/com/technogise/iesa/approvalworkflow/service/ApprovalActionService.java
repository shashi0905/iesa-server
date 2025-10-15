package com.technogise.iesa.approvalworkflow.service;

import com.technogise.iesa.approvalworkflow.domain.ApprovalAction;
import com.technogise.iesa.approvalworkflow.domain.ApprovalActionType;
import com.technogise.iesa.approvalworkflow.domain.ApprovalStep;
import com.technogise.iesa.approvalworkflow.dto.ApprovalActionDto;
import com.technogise.iesa.approvalworkflow.dto.ApprovalWorkflowMapper;
import com.technogise.iesa.approvalworkflow.dto.CreateApprovalActionRequest;
import com.technogise.iesa.approvalworkflow.repository.ApprovalActionRepository;
import com.technogise.iesa.approvalworkflow.repository.ApprovalStepRepository;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApprovalActionService {

    private final ApprovalActionRepository actionRepository;
    private final ExpenseRepository expenseRepository;
    private final ApprovalStepRepository stepRepository;
    private final UserRepository userRepository;
    private final ApprovalWorkflowMapper mapper;

    @Transactional(readOnly = true)
    public List<ApprovalActionDto> getActionsForExpense(UUID expenseId) {
        return mapper.toActionDtoList(actionRepository.findByExpenseIdOrderByActionDateDesc(expenseId));
    }

    @Transactional(readOnly = true)
    public List<ApprovalActionDto> getActionsByApprover(UUID approverId) {
        return mapper.toActionDtoList(actionRepository.findByApproverId(approverId));
    }

    @Transactional(readOnly = true)
    public ApprovalActionDto getLatestActionForExpense(UUID expenseId) {
        ApprovalAction action = actionRepository.findLatestByExpenseId(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("No actions found for expense: " + expenseId));
        return mapper.toDto(action);
    }

    @Transactional(readOnly = true)
    public List<ApprovalActionDto> getPendingDelegations(UUID userId) {
        return mapper.toActionDtoList(actionRepository.findPendingDelegationsByUserId(userId));
    }

    public ApprovalActionDto createApprovalAction(CreateApprovalActionRequest request) {
        log.info("Creating approval action for expense: {}, action: {}", request.getExpenseId(), request.getAction());

        // Get current user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User approver = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // Get expense
        Expense expense = expenseRepository.findById(request.getExpenseId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + request.getExpenseId()));

        // Get step if provided
        ApprovalStep step = null;
        if (request.getStepId() != null) {
            step = stepRepository.findById(request.getStepId())
                    .orElseThrow(() -> new ResourceNotFoundException("Step not found with id: " + request.getStepId()));
        }

        // Get delegated user if provided
        User delegatedTo = null;
        if (request.getDelegatedToId() != null) {
            delegatedTo = userRepository.findById(request.getDelegatedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getDelegatedToId()));
        }

        // Create approval action
        ApprovalAction action = ApprovalAction.builder()
                .expense(expense)
                .step(step)
                .approver(approver)
                .action(ApprovalActionType.valueOf(request.getAction()))
                .comment(request.getComment())
                .actionDate(Instant.now())
                .delegatedTo(delegatedTo)
                .build();

        action = actionRepository.save(action);
        log.info("Approval action created successfully with id: {}", action.getId());

        return mapper.toDto(action);
    }

    @Transactional(readOnly = true)
    public boolean hasApprovedAtStep(UUID expenseId, UUID stepId) {
        return actionRepository.hasApprovedAtStep(expenseId, stepId);
    }

    @Transactional(readOnly = true)
    public long countActionsByApproverAndType(UUID approverId, ApprovalActionType actionType) {
        return actionRepository.countByApproverIdAndActionType(approverId, actionType);
    }
}
