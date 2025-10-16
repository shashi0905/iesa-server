package com.technogise.iesa.budgetmanagement.service;

import com.technogise.iesa.budgetmanagement.domain.Budget;
import com.technogise.iesa.budgetmanagement.domain.BudgetPeriod;
import com.technogise.iesa.budgetmanagement.dto.*;
import com.technogise.iesa.budgetmanagement.repository.BudgetRepository;
import com.technogise.iesa.departmentmanagement.domain.Department;
import com.technogise.iesa.departmentmanagement.repository.DepartmentRepository;
import com.technogise.iesa.segmentmanagement.domain.Segment;
import com.technogise.iesa.segmentmanagement.domain.SegmentType;
import com.technogise.iesa.segmentmanagement.repository.SegmentRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private SegmentRepository segmentRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private BudgetMapper budgetMapper;

    @InjectMocks
    private BudgetService budgetService;

    private Budget budget;
    private BudgetDto budgetDto;
    private CreateBudgetRequest createRequest;
    private UpdateBudgetRequest updateRequest;
    private UUID budgetId;
    private UUID segmentId;
    private UUID departmentId;
    private Segment segment;
    private Department department;

    @BeforeEach
    void setUp() {
        budgetId = UUID.randomUUID();
        segmentId = UUID.randomUUID();
        departmentId = UUID.randomUUID();

        segment = Segment.builder()
                .id(segmentId)
                .name("Marketing")
                .code("MKT-001")
                .segmentType(SegmentType.CATEGORY)
                .isActive(true)
                .build();

        department = Department.builder()
                .id(departmentId)
                .name("Marketing Department")
                .code("DEPT-MKT")
                .isActive(true)
                .build();

        budget = Budget.builder()
                .id(budgetId)
                .name("Q1 Marketing Budget")
                .segment(segment)
                .department(department)
                .period(BudgetPeriod.QUARTERLY)
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 3, 31))
                .allocatedAmount(new BigDecimal("50000.00"))
                .consumedAmount(new BigDecimal("25000.00"))
                .isActive(true)
                .build();

        budgetDto = BudgetDto.builder()
                .id(budgetId)
                .name("Q1 Marketing Budget")
                .segmentId(segmentId)
                .segmentName("Marketing")
                .departmentId(departmentId)
                .departmentName("Marketing Department")
                .period(BudgetPeriod.QUARTERLY.name())
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 3, 31))
                .allocatedAmount(new BigDecimal("50000.00"))
                .consumedAmount(new BigDecimal("25000.00"))
                .remainingAmount(new BigDecimal("25000.00"))
                .utilizationPercentage(new BigDecimal("50.00"))
                .isActive(true)
                .build();

        createRequest = CreateBudgetRequest.builder()
                .name("Q1 Marketing Budget")
                .segmentId(segmentId)
                .departmentId(departmentId)
                .period(BudgetPeriod.QUARTERLY.name())
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 3, 31))
                .allocatedAmount(new BigDecimal("50000.00"))
                .build();

        updateRequest = UpdateBudgetRequest.builder()
                .name("Updated Q1 Marketing Budget")
                .allocatedAmount(new BigDecimal("60000.00"))
                .build();
    }

    @Test
    void getAllBudgets_ShouldReturnAllBudgets() {
        // Arrange
        List<Budget> budgets = Arrays.asList(budget);
        List<BudgetDto> budgetDtos = Arrays.asList(budgetDto);

        when(budgetRepository.findAllNotDeleted()).thenReturn(budgets);
        when(budgetMapper.toDtoList(budgets)).thenReturn(budgetDtos);

        // Act
        List<BudgetDto> result = budgetService.getAllBudgets();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Q1 Marketing Budget");
        verify(budgetRepository, times(1)).findAllNotDeleted();
        verify(budgetMapper, times(1)).toDtoList(budgets);
    }

    @Test
    void getAllActiveBudgets_ShouldReturnOnlyActiveBudgets() {
        // Arrange
        List<Budget> budgets = Arrays.asList(budget);
        List<BudgetDto> budgetDtos = Arrays.asList(budgetDto);

        when(budgetRepository.findAllActive()).thenReturn(budgets);
        when(budgetMapper.toDtoList(budgets)).thenReturn(budgetDtos);

        // Act
        List<BudgetDto> result = budgetService.getAllActiveBudgets();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isTrue();
        verify(budgetRepository, times(1)).findAllActive();
        verify(budgetMapper, times(1)).toDtoList(budgets);
    }

    @Test
    void getBudgetById_WhenExists_ShouldReturnBudget() {
        // Arrange
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(budgetMapper.toDto(budget)).thenReturn(budgetDto);

        // Act
        BudgetDto result = budgetService.getBudgetById(budgetId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(budgetId);
        assertThat(result.getName()).isEqualTo("Q1 Marketing Budget");
        verify(budgetRepository, times(1)).findById(budgetId);
    }

    @Test
    void getBudgetById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> budgetService.getBudgetById(budgetId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Budget not found with id");

        verify(budgetRepository, times(1)).findById(budgetId);
        verify(budgetMapper, never()).toDto(any(Budget.class));
    }

    @Test
    void getBudgetsBySegment_ShouldReturnBudgetsForSegment() {
        // Arrange
        List<Budget> budgets = Arrays.asList(budget);
        List<BudgetDto> budgetDtos = Arrays.asList(budgetDto);

        when(budgetRepository.findBySegmentId(segmentId)).thenReturn(budgets);
        when(budgetMapper.toDtoList(budgets)).thenReturn(budgetDtos);

        // Act
        List<BudgetDto> result = budgetService.getBudgetsBySegment(segmentId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSegmentId()).isEqualTo(segmentId);
        verify(budgetRepository, times(1)).findBySegmentId(segmentId);
        verify(budgetMapper, times(1)).toDtoList(budgets);
    }

    @Test
    void getBudgetsByDepartment_ShouldReturnBudgetsForDepartment() {
        // Arrange
        List<Budget> budgets = Arrays.asList(budget);
        List<BudgetDto> budgetDtos = Arrays.asList(budgetDto);

        when(budgetRepository.findByDepartmentId(departmentId)).thenReturn(budgets);
        when(budgetMapper.toDtoList(budgets)).thenReturn(budgetDtos);

        // Act
        List<BudgetDto> result = budgetService.getBudgetsByDepartment(departmentId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentId()).isEqualTo(departmentId);
        verify(budgetRepository, times(1)).findByDepartmentId(departmentId);
        verify(budgetMapper, times(1)).toDtoList(budgets);
    }

    @Test
    void getBudgetsByPeriod_ShouldReturnBudgetsForPeriod() {
        // Arrange
        List<Budget> budgets = Arrays.asList(budget);
        List<BudgetDto> budgetDtos = Arrays.asList(budgetDto);

        when(budgetRepository.findByPeriod(BudgetPeriod.QUARTERLY)).thenReturn(budgets);
        when(budgetMapper.toDtoList(budgets)).thenReturn(budgetDtos);

        // Act
        List<BudgetDto> result = budgetService.getBudgetsByPeriod(BudgetPeriod.QUARTERLY);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPeriod()).isEqualTo(BudgetPeriod.QUARTERLY.name());
        verify(budgetRepository, times(1)).findByPeriod(BudgetPeriod.QUARTERLY);
        verify(budgetMapper, times(1)).toDtoList(budgets);
    }

    @Test
    void getCurrentBudgets_ShouldReturnBudgetsForCurrentDate() {
        // Arrange
        LocalDate currentDate = LocalDate.now();
        List<Budget> budgets = Arrays.asList(budget);
        List<BudgetDto> budgetDtos = Arrays.asList(budgetDto);

        when(budgetRepository.findCurrentBudgets(currentDate)).thenReturn(budgets);
        when(budgetMapper.toDtoList(budgets)).thenReturn(budgetDtos);

        // Act
        List<BudgetDto> result = budgetService.getCurrentBudgets();

        // Assert
        assertThat(result).hasSize(1);
        verify(budgetRepository, times(1)).findCurrentBudgets(currentDate);
        verify(budgetMapper, times(1)).toDtoList(budgets);
    }

    @Test
    void createBudget_WithValidData_ShouldCreateSuccessfully() {
        // Arrange
        when(segmentRepository.findById(segmentId)).thenReturn(Optional.of(segment));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(budgetRepository.existsByNameAndPeriod(anyString(), any(), any(), any())).thenReturn(false);
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);
        when(budgetMapper.toDto(budget)).thenReturn(budgetDto);

        // Act
        BudgetDto result = budgetService.createBudget(createRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Q1 Marketing Budget");
        assertThat(result.getAllocatedAmount()).isEqualTo(new BigDecimal("50000.00"));
        verify(segmentRepository, times(1)).findById(segmentId);
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    void createBudget_WithDuplicateBudget_ShouldThrowException() {
        // Arrange
        when(budgetRepository.existsByNameAndPeriod(anyString(), any(), any(), any())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> budgetService.createBudget(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(budgetRepository, never()).save(any());
    }

    @Test
    void createBudget_WithInvalidDateRange_ShouldThrowException() {
        // Arrange
        CreateBudgetRequest invalidRequest = CreateBudgetRequest.builder()
                .name("Invalid Budget")
                .segmentId(segmentId)
                .departmentId(departmentId)
                .period(BudgetPeriod.QUARTERLY.name())
                .startDate(LocalDate.of(2025, 3, 31))
                .endDate(LocalDate.of(2025, 1, 1))
                .allocatedAmount(new BigDecimal("50000.00"))
                .build();

        // Act & Assert
        assertThatThrownBy(() -> budgetService.createBudget(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start date must be before end date");

        verify(budgetRepository, never()).save(any());
    }

    @Test
    void updateBudget_WithValidData_ShouldUpdateSuccessfully() {
        // Arrange
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);
        when(budgetMapper.toDto(budget)).thenReturn(budgetDto);

        // Act
        BudgetDto result = budgetService.updateBudget(budgetId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        verify(budgetRepository, times(1)).findById(budgetId);
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    void updateBudget_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> budgetService.updateBudget(budgetId, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Budget not found");

        verify(budgetRepository, times(1)).findById(budgetId);
        verify(budgetRepository, never()).save(any());
    }

    @Test
    void deleteBudget_WhenExists_ShouldSoftDelete() {
        // Arrange
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        // Act
        budgetService.deleteBudget(budgetId);

        // Assert
        assertThat(budget.getDeletedAt()).isNotNull();
        verify(budgetRepository, times(1)).findById(budgetId);
        verify(budgetRepository, times(1)).save(budget);
    }

    @Test
    void activateBudget_WhenExists_ShouldActivate() {
        // Arrange
        budget.setIsActive(false);
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(budget)).thenReturn(budget);
        when(budgetMapper.toDto(budget)).thenReturn(budgetDto);

        // Act
        BudgetDto result = budgetService.activateBudget(budgetId);

        // Assert
        assertThat(budget.getIsActive()).isTrue();
        verify(budgetRepository, times(1)).findById(budgetId);
        verify(budgetRepository, times(1)).save(budget);
    }

    @Test
    void deactivateBudget_WhenExists_ShouldDeactivate() {
        // Arrange
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(budget)).thenReturn(budget);
        when(budgetMapper.toDto(budget)).thenReturn(budgetDto);

        // Act
        BudgetDto result = budgetService.deactivateBudget(budgetId);

        // Assert
        assertThat(budget.getIsActive()).isFalse();
        verify(budgetRepository, times(1)).findById(budgetId);
        verify(budgetRepository, times(1)).save(budget);
    }

    @Test
    void updateConsumption_ShouldUpdateConsumedAmount() {
        // Arrange
        BigDecimal additionalAmount = new BigDecimal("5000.00");
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(budget)).thenReturn(budget);
        when(budgetMapper.toDto(budget)).thenReturn(budgetDto);

        // Act
        BudgetDto result = budgetService.updateConsumption(budgetId, additionalAmount);

        // Assert
        assertThat(budget.getConsumedAmount()).isEqualTo(new BigDecimal("30000.00"));
        verify(budgetRepository, times(1)).save(budget);
    }

    @Test
    void getBudgetUtilization_ShouldCalculateUtilizationPercentage() {
        // Arrange
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        // Act
        BigDecimal utilization = budgetService.getBudgetUtilization(budgetId);

        // Assert
        assertThat(utilization).isEqualByComparingTo(new BigDecimal("50.00"));
        verify(budgetRepository, times(1)).findById(budgetId);
    }

    @Test
    void getRemainingAmount_ShouldCalculateRemainingBudget() {
        // Arrange
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        // Act
        BigDecimal remaining = budgetService.getRemainingAmount(budgetId);

        // Assert
        assertThat(remaining).isEqualByComparingTo(new BigDecimal("25000.00"));
        verify(budgetRepository, times(1)).findById(budgetId);
    }

    @Test
    void checkBudgetAvailability_WhenSufficientBudget_ShouldReturnTrue() {
        // Arrange
        BigDecimal requestedAmount = new BigDecimal("20000.00");
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        // Act
        boolean available = budgetService.checkBudgetAvailability(budgetId, requestedAmount);

        // Assert
        assertThat(available).isTrue();
    }

    @Test
    void checkBudgetAvailability_WhenInsufficientBudget_ShouldReturnFalse() {
        // Arrange
        BigDecimal requestedAmount = new BigDecimal("30000.00");
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        // Act
        boolean available = budgetService.checkBudgetAvailability(budgetId, requestedAmount);

        // Assert
        assertThat(available).isFalse();
    }
}
