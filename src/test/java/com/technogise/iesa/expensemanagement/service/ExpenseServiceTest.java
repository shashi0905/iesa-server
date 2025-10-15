package com.technogise.iesa.expensemanagement.service;

import com.technogise.iesa.expensemanagement.domain.*;
import com.technogise.iesa.expensemanagement.dto.*;
import com.technogise.iesa.expensemanagement.repository.ExpenseRepository;
import com.technogise.iesa.expensemanagement.repository.SegmentAllocationRepository;
import com.technogise.iesa.segmentmanagement.domain.Segment;
import com.technogise.iesa.segmentmanagement.domain.SegmentType;
import com.technogise.iesa.segmentmanagement.repository.SegmentRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import com.technogise.iesa.usermanagement.domain.User;
import com.technogise.iesa.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private SegmentAllocationRepository segmentAllocationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SegmentRepository segmentRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseService expenseService;

    private Expense expense;
    private ExpenseDto expenseDto;
    private CreateExpenseRequest createRequest;
    private UpdateExpenseRequest updateRequest;
    private UUID expenseId;
    private UUID userId;
    private UUID segmentId;
    private User user;
    private Segment segment;
    private SegmentAllocation allocation;

    @BeforeEach
    void setUp() {
        expenseId = UUID.randomUUID();
        userId = UUID.randomUUID();
        segmentId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();

        segment = Segment.builder()
                .id(segmentId)
                .name("Engineering")
                .code("ENG-001")
                .segmentType(SegmentType.COST_CENTER)
                .isActive(true)
                .build();

        allocation = SegmentAllocation.builder()
                .id(UUID.randomUUID())
                .segment(segment)
                .amount(new BigDecimal("1000.00"))
                .percentage(new BigDecimal("100.00"))
                .description("Full allocation to Engineering")
                .build();

        expense = Expense.builder()
                .id(expenseId)
                .submitter(user)
                .expenseDate(LocalDate.of(2025, 10, 1))
                .vendor("Test Vendor")
                .totalAmount(new BigDecimal("1000.00"))
                .currency("USD")
                .description("Test expense")
                .status(ExpenseStatus.DRAFT)
                .build();
        expense.addSegmentAllocation(allocation);

        expenseDto = ExpenseDto.builder()
                .id(expenseId)
                .submitterId(userId)
                .expenseDate(LocalDate.of(2025, 10, 1))
                .vendor("Test Vendor")
                .totalAmount(new BigDecimal("1000.00"))
                .currency("USD")
                .description("Test expense")
                .status(ExpenseStatus.DRAFT)
                .build();

        SegmentAllocationRequest allocationRequest = SegmentAllocationRequest.builder()
                .segmentId(segmentId)
                .percentage(new BigDecimal("100.00"))
                .description("Full allocation to Engineering")
                .build();

        createRequest = CreateExpenseRequest.builder()
                .expenseDate(LocalDate.of(2025, 10, 1))
                .vendor("Test Vendor")
                .totalAmount(new BigDecimal("1000.00"))
                .currency("USD")
                .description("Test expense")
                .segmentAllocations(Collections.singletonList(allocationRequest))
                .build();

        updateRequest = UpdateExpenseRequest.builder()
                .expenseDate(LocalDate.of(2025, 10, 2))
                .vendor("Updated Vendor")
                .description("Updated description")
                .build();
    }

    @Test
    void getAllExpenses_ShouldReturnAllExpenses() {
        // Arrange
        List<Expense> expenses = Arrays.asList(expense);
        List<ExpenseDto> expenseDtos = Arrays.asList(expenseDto);

        when(expenseRepository.findAllNotDeleted()).thenReturn(expenses);
        when(expenseMapper.toDtoList(expenses)).thenReturn(expenseDtos);

        // Act
        List<ExpenseDto> result = expenseService.getAllExpenses();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVendor()).isEqualTo("Test Vendor");
        verify(expenseRepository, times(1)).findAllNotDeleted();
        verify(expenseMapper, times(1)).toDtoList(expenses);
    }

    @Test
    void getExpenseById_WhenExists_ShouldReturnExpense() {
        // Arrange
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(expenseMapper.toDto(expense)).thenReturn(expenseDto);

        // Act
        ExpenseDto result = expenseService.getExpenseById(expenseId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expenseId);
        assertThat(result.getVendor()).isEqualTo("Test Vendor");
        verify(expenseRepository, times(1)).findById(expenseId);
    }

    @Test
    void getExpenseById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> expenseService.getExpenseById(expenseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Expense not found with id");

        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseMapper, never()).toDto(any());
    }

    @Test
    void getExpensesBySubmitter_ShouldReturnExpensesBySubmitter() {
        // Arrange
        List<Expense> expenses = Arrays.asList(expense);
        List<ExpenseDto> expenseDtos = Arrays.asList(expenseDto);

        when(expenseRepository.findBySubmitterId(userId)).thenReturn(expenses);
        when(expenseMapper.toDtoList(expenses)).thenReturn(expenseDtos);

        // Act
        List<ExpenseDto> result = expenseService.getExpensesBySubmitter(userId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSubmitterId()).isEqualTo(userId);
        verify(expenseRepository, times(1)).findBySubmitterId(userId);
    }

    @Test
    void getExpensesByStatus_ShouldReturnExpensesByStatus() {
        // Arrange
        List<Expense> expenses = Arrays.asList(expense);
        List<ExpenseDto> expenseDtos = Arrays.asList(expenseDto);

        when(expenseRepository.findByStatus(ExpenseStatus.DRAFT)).thenReturn(expenses);
        when(expenseMapper.toDtoList(expenses)).thenReturn(expenseDtos);

        // Act
        List<ExpenseDto> result = expenseService.getExpensesByStatus(ExpenseStatus.DRAFT);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ExpenseStatus.DRAFT);
        verify(expenseRepository, times(1)).findByStatus(ExpenseStatus.DRAFT);
    }

    @Test
    void getPendingApprovals_ShouldReturnPendingExpenses() {
        // Arrange
        expense.setStatus(ExpenseStatus.SUBMITTED);
        expenseDto.setStatus(ExpenseStatus.SUBMITTED);
        List<Expense> expenses = Arrays.asList(expense);
        List<ExpenseDto> expenseDtos = Arrays.asList(expenseDto);

        when(expenseRepository.findPendingApprovals()).thenReturn(expenses);
        when(expenseMapper.toDtoList(expenses)).thenReturn(expenseDtos);

        // Act
        List<ExpenseDto> result = expenseService.getPendingApprovals();

        // Assert
        assertThat(result).hasSize(1);
        verify(expenseRepository, times(1)).findPendingApprovals();
    }

    @Test
    void createExpense_WithValidData_ShouldCreateSuccessfully() {
        // Arrange
        setupSecurityContext("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(segmentRepository.findById(segmentId)).thenReturn(Optional.of(segment));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(expenseMapper.toDto(expense)).thenReturn(expenseDto);

        // Act
        ExpenseDto result = expenseService.createExpense(createRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getVendor()).isEqualTo("Test Vendor");
        assertThat(result.getTotalAmount()).isEqualTo(new BigDecimal("1000.00"));
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(segmentRepository, times(1)).findById(segmentId);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void createExpense_WithInvalidAllocations_ShouldThrowException() {
        // Arrange
        setupSecurityContext("testuser");

        SegmentAllocationRequest invalidAllocation = SegmentAllocationRequest.builder()
                .segmentId(segmentId)
                .percentage(new BigDecimal("50.00"))
                .build();

        CreateExpenseRequest invalidRequest = CreateExpenseRequest.builder()
                .expenseDate(LocalDate.of(2025, 10, 1))
                .vendor("Test Vendor")
                .totalAmount(new BigDecimal("1000.00"))
                .segmentAllocations(Collections.singletonList(invalidAllocation))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> expenseService.createExpense(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must sum to 100%");

        verify(expenseRepository, never()).save(any());
    }

    @Test
    void createExpense_WithEmptyAllocations_ShouldThrowException() {
        // Arrange
        setupSecurityContext("testuser");

        CreateExpenseRequest invalidRequest = CreateExpenseRequest.builder()
                .expenseDate(LocalDate.of(2025, 10, 1))
                .vendor("Test Vendor")
                .totalAmount(new BigDecimal("1000.00"))
                .segmentAllocations(Collections.emptyList())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> expenseService.createExpense(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("At least one segment allocation is required");

        verify(expenseRepository, never()).save(any());
    }

    @Test
    void updateExpense_WithValidData_ShouldUpdateSuccessfully() {
        // Arrange
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(expenseMapper.toDto(expense)).thenReturn(expenseDto);

        // Act
        ExpenseDto result = expenseService.updateExpense(expenseId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void updateExpense_WhenNotEditable_ShouldThrowException() {
        // Arrange
        expense.setStatus(ExpenseStatus.APPROVED);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        // Act & Assert
        assertThatThrownBy(() -> expenseService.updateExpense(expenseId, updateRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot be edited");

        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void deleteExpense_WhenEditable_ShouldSoftDelete() {
        // Arrange
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        // Act
        expenseService.deleteExpense(expenseId);

        // Assert
        assertThat(expense.getDeletedAt()).isNotNull();
        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, times(1)).save(expense);
    }

    @Test
    void deleteExpense_WhenNotEditable_ShouldThrowException() {
        // Arrange
        expense.setStatus(ExpenseStatus.APPROVED);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        // Act & Assert
        assertThatThrownBy(() -> expenseService.deleteExpense(expenseId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot be deleted");

        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void submitExpense_WhenCanBeSubmitted_ShouldSubmit() {
        // Arrange
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(expense)).thenReturn(expense);
        when(expenseMapper.toDto(expense)).thenReturn(expenseDto);

        // Act
        ExpenseDto result = expenseService.submitExpense(expenseId);

        // Assert
        assertThat(expense.getStatus()).isEqualTo(ExpenseStatus.SUBMITTED);
        assertThat(expense.getSubmissionDate()).isNotNull();
        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, times(1)).save(expense);
    }

    @Test
    void submitExpense_WhenCannotBeSubmitted_ShouldThrowException() {
        // Arrange
        expense.setStatus(ExpenseStatus.APPROVED);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        // Act & Assert
        assertThatThrownBy(() -> expenseService.submitExpense(expenseId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot be submitted");

        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void approveExpense_WhenCanBeApproved_ShouldApprove() {
        // Arrange
        expense.setStatus(ExpenseStatus.SUBMITTED);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(expense)).thenReturn(expense);
        when(expenseMapper.toDto(expense)).thenReturn(expenseDto);

        // Act
        ExpenseDto result = expenseService.approveExpense(expenseId);

        // Assert
        assertThat(expense.getStatus()).isEqualTo(ExpenseStatus.APPROVED);
        assertThat(expense.getApprovalDate()).isNotNull();
        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, times(1)).save(expense);
    }

    @Test
    void approveExpense_WhenCannotBeApproved_ShouldThrowException() {
        // Arrange
        expense.setStatus(ExpenseStatus.DRAFT);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        // Act & Assert
        assertThatThrownBy(() -> expenseService.approveExpense(expenseId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot be approved");

        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void rejectExpense_WhenCanBeRejected_ShouldReject() {
        // Arrange
        expense.setStatus(ExpenseStatus.SUBMITTED);
        String rejectionReason = "Insufficient documentation";
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(expense)).thenReturn(expense);
        when(expenseMapper.toDto(expense)).thenReturn(expenseDto);

        // Act
        ExpenseDto result = expenseService.rejectExpense(expenseId, rejectionReason);

        // Assert
        assertThat(expense.getStatus()).isEqualTo(ExpenseStatus.REJECTED);
        assertThat(expense.getRejectionReason()).isEqualTo(rejectionReason);
        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, times(1)).save(expense);
    }

    @Test
    void rejectExpense_WhenCannotBeRejected_ShouldThrowException() {
        // Arrange
        expense.setStatus(ExpenseStatus.APPROVED);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        // Act & Assert
        assertThatThrownBy(() -> expenseService.rejectExpense(expenseId, "Some reason"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot be rejected");

        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, never()).save(any());
    }

    private void setupSecurityContext(String username) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
