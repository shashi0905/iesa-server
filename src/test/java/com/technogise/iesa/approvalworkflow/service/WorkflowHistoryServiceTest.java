package com.technogise.iesa.approvalworkflow.service;

import com.technogise.iesa.approvalworkflow.domain.WorkflowHistory;
import com.technogise.iesa.approvalworkflow.dto.ApprovalWorkflowMapper;
import com.technogise.iesa.approvalworkflow.dto.WorkflowHistoryDto;
import com.technogise.iesa.approvalworkflow.repository.WorkflowHistoryRepository;
import com.technogise.iesa.expensemanagement.domain.Expense;
import com.technogise.iesa.expensemanagement.domain.ExpenseStatus;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import com.technogise.iesa.usermanagement.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowHistoryServiceTest {

    @Mock
    private WorkflowHistoryRepository historyRepository;

    @Mock
    private ApprovalWorkflowMapper mapper;

    @InjectMocks
    private WorkflowHistoryService historyService;

    private WorkflowHistory history;
    private WorkflowHistoryDto historyDto;
    private UUID historyId;
    private UUID expenseId;
    private UUID actorId;
    private User actor;
    private Expense expense;

    @BeforeEach
    void setUp() {
        historyId = UUID.randomUUID();
        expenseId = UUID.randomUUID();
        actorId = UUID.randomUUID();

        actor = User.builder()
                .id(actorId)
                .username("approver")
                .email("approver@example.com")
                .firstName("Approver")
                .lastName("User")
                .build();

        expense = Expense.builder()
                .id(expenseId)
                .submitter(actor)
                .expenseDate(LocalDate.of(2025, 10, 1))
                .vendor("Test Vendor")
                .totalAmount(new BigDecimal("1000.00"))
                .currency("USD")
                .description("Test expense")
                .status(ExpenseStatus.APPROVED)
                .build();

        history = WorkflowHistory.builder()
                .id(historyId)
                .expense(expense)
                .fromStatus(ExpenseStatus.SUBMITTED)
                .toStatus(ExpenseStatus.APPROVED)
                .actor(actor)
                .comment("Approved by manager")
                .timestamp(Instant.now())
                .build();

        historyDto = WorkflowHistoryDto.builder()
                .id(historyId)
                .expenseId(expenseId)
                .fromStatus(ExpenseStatus.SUBMITTED.name())
                .toStatus(ExpenseStatus.APPROVED.name())
                .actorId(actorId)
                .actorName("Approver User")
                .comment("Approved by manager")
                .timestamp(Instant.now())
                .build();
    }

    @Test
    void getHistoryForExpense_ShouldReturnHistoryOrderedByTimestamp() {
        // Arrange
        List<WorkflowHistory> histories = Arrays.asList(history);
        List<WorkflowHistoryDto> historyDtos = Arrays.asList(historyDto);

        when(historyRepository.findByExpenseIdOrderByTimestampDesc(expenseId)).thenReturn(histories);
        when(mapper.toHistoryDtoList(histories)).thenReturn(historyDtos);

        // Act
        List<WorkflowHistoryDto> result = historyService.getHistoryForExpense(expenseId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getExpenseId()).isEqualTo(expenseId);
        assertThat(result.get(0).getFromStatus()).isEqualTo(ExpenseStatus.SUBMITTED.name());
        assertThat(result.get(0).getToStatus()).isEqualTo(ExpenseStatus.APPROVED.name());
        verify(historyRepository, times(1)).findByExpenseIdOrderByTimestampDesc(expenseId);
        verify(mapper, times(1)).toHistoryDtoList(histories);
    }

    @Test
    void getHistoryByActor_ShouldReturnHistoryForActor() {
        // Arrange
        List<WorkflowHistory> histories = Arrays.asList(history);
        List<WorkflowHistoryDto> historyDtos = Arrays.asList(historyDto);

        when(historyRepository.findByActorId(actorId)).thenReturn(histories);
        when(mapper.toHistoryDtoList(histories)).thenReturn(historyDtos);

        // Act
        List<WorkflowHistoryDto> result = historyService.getHistoryByActor(actorId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActorId()).isEqualTo(actorId);
        verify(historyRepository, times(1)).findByActorId(actorId);
    }

    @Test
    void getLatestHistoryForExpense_WhenExists_ShouldReturnLatest() {
        // Arrange
        when(historyRepository.findLatestByExpenseId(expenseId)).thenReturn(Optional.of(history));
        when(mapper.toDto(history)).thenReturn(historyDto);

        // Act
        WorkflowHistoryDto result = historyService.getLatestHistoryForExpense(expenseId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(historyId);
        assertThat(result.getExpenseId()).isEqualTo(expenseId);
        verify(historyRepository, times(1)).findLatestByExpenseId(expenseId);
    }

    @Test
    void getLatestHistoryForExpense_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(historyRepository.findLatestByExpenseId(expenseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> historyService.getLatestHistoryForExpense(expenseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No history found for expense");

        verify(historyRepository, times(1)).findLatestByExpenseId(expenseId);
        verify(mapper, never()).toDto(any(WorkflowHistory.class));
    }

    @Test
    void getApprovalTransitions_ShouldReturnApprovals() {
        // Arrange
        List<WorkflowHistory> histories = Arrays.asList(history);
        List<WorkflowHistoryDto> historyDtos = Arrays.asList(historyDto);

        when(historyRepository.findApprovalTransitions()).thenReturn(histories);
        when(mapper.toHistoryDtoList(histories)).thenReturn(historyDtos);

        // Act
        List<WorkflowHistoryDto> result = historyService.getApprovalTransitions();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getToStatus()).isEqualTo(ExpenseStatus.APPROVED.name());
        verify(historyRepository, times(1)).findApprovalTransitions();
    }

    @Test
    void getRejectionTransitions_ShouldReturnRejections() {
        // Arrange
        WorkflowHistory rejectionHistory = WorkflowHistory.builder()
                .id(UUID.randomUUID())
                .expense(expense)
                .fromStatus(ExpenseStatus.SUBMITTED)
                .toStatus(ExpenseStatus.REJECTED)
                .actor(actor)
                .comment("Rejected due to insufficient documentation")
                .timestamp(Instant.now())
                .build();

        WorkflowHistoryDto rejectionDto = WorkflowHistoryDto.builder()
                .id(rejectionHistory.getId())
                .expenseId(expenseId)
                .fromStatus(ExpenseStatus.SUBMITTED.name())
                .toStatus(ExpenseStatus.REJECTED.name())
                .actorId(actorId)
                .comment("Rejected due to insufficient documentation")
                .timestamp(Instant.now())
                .build();

        List<WorkflowHistory> histories = Arrays.asList(rejectionHistory);
        List<WorkflowHistoryDto> historyDtos = Arrays.asList(rejectionDto);

        when(historyRepository.findRejectionTransitions()).thenReturn(histories);
        when(mapper.toHistoryDtoList(histories)).thenReturn(historyDtos);

        // Act
        List<WorkflowHistoryDto> result = historyService.getRejectionTransitions();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getToStatus()).isEqualTo(ExpenseStatus.REJECTED.name());
        verify(historyRepository, times(1)).findRejectionTransitions();
    }

    @Test
    void createHistoryEntry_ShouldCreateAndReturnHistory() {
        // Arrange
        ExpenseStatus fromStatus = ExpenseStatus.SUBMITTED;
        ExpenseStatus toStatus = ExpenseStatus.APPROVED;
        String comment = "Approved by manager";

        when(historyRepository.save(any(WorkflowHistory.class))).thenReturn(history);
        when(mapper.toDto(history)).thenReturn(historyDto);

        // Act
        WorkflowHistoryDto result = historyService.createHistoryEntry(expense, fromStatus, toStatus, actor, comment);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFromStatus()).isEqualTo(fromStatus.name());
        assertThat(result.getToStatus()).isEqualTo(toStatus.name());
        assertThat(result.getComment()).isEqualTo(comment);
        verify(historyRepository, times(1)).save(any(WorkflowHistory.class));
        verify(mapper, times(1)).toDto(history);
    }

    @Test
    void createHistoryEntry_WithNullFromStatus_ShouldCreate() {
        // Arrange
        ExpenseStatus toStatus = ExpenseStatus.SUBMITTED;
        String comment = "Submitted for approval";

        WorkflowHistory submissionHistory = WorkflowHistory.builder()
                .id(historyId)
                .expense(expense)
                .fromStatus(null)
                .toStatus(toStatus)
                .actor(actor)
                .comment(comment)
                .timestamp(Instant.now())
                .build();

        WorkflowHistoryDto submissionDto = WorkflowHistoryDto.builder()
                .id(historyId)
                .expenseId(expenseId)
                .fromStatus(null)
                .toStatus(toStatus.name())
                .actorId(actorId)
                .comment(comment)
                .timestamp(Instant.now())
                .build();

        when(historyRepository.save(any(WorkflowHistory.class))).thenReturn(submissionHistory);
        when(mapper.toDto(submissionHistory)).thenReturn(submissionDto);

        // Act
        WorkflowHistoryDto result = historyService.createHistoryEntry(expense, null, toStatus, actor, comment);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFromStatus()).isNull();
        assertThat(result.getToStatus()).isEqualTo(toStatus.name());
        verify(historyRepository, times(1)).save(any(WorkflowHistory.class));
    }

    @Test
    void createHistoryEntry_WithNullComment_ShouldCreate() {
        // Arrange
        ExpenseStatus fromStatus = ExpenseStatus.SUBMITTED;
        ExpenseStatus toStatus = ExpenseStatus.APPROVED;

        WorkflowHistory historyWithoutComment = WorkflowHistory.builder()
                .id(historyId)
                .expense(expense)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .actor(actor)
                .comment(null)
                .timestamp(Instant.now())
                .build();

        WorkflowHistoryDto dtoWithoutComment = WorkflowHistoryDto.builder()
                .id(historyId)
                .expenseId(expenseId)
                .fromStatus(fromStatus.name())
                .toStatus(toStatus.name())
                .actorId(actorId)
                .comment(null)
                .timestamp(Instant.now())
                .build();

        when(historyRepository.save(any(WorkflowHistory.class))).thenReturn(historyWithoutComment);
        when(mapper.toDto(historyWithoutComment)).thenReturn(dtoWithoutComment);

        // Act
        WorkflowHistoryDto result = historyService.createHistoryEntry(expense, fromStatus, toStatus, actor, null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getComment()).isNull();
        verify(historyRepository, times(1)).save(any(WorkflowHistory.class));
    }
}
