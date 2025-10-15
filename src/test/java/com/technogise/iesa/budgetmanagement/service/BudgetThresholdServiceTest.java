package com.technogise.iesa.budgetmanagement.service;

import com.technogise.iesa.budgetmanagement.domain.Budget;
import com.technogise.iesa.budgetmanagement.domain.BudgetPeriod;
import com.technogise.iesa.budgetmanagement.domain.BudgetThreshold;
import com.technogise.iesa.budgetmanagement.dto.*;
import com.technogise.iesa.budgetmanagement.repository.BudgetRepository;
import com.technogise.iesa.budgetmanagement.repository.BudgetThresholdRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import com.technogise.iesa.usermanagement.domain.User;
import com.technogise.iesa.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class BudgetThresholdServiceTest {

    @Mock
    private BudgetThresholdRepository thresholdRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BudgetMapper budgetMapper;

    @InjectMocks
    private BudgetThresholdService thresholdService;

    private BudgetThreshold threshold;
    private BudgetThresholdDto thresholdDto;
    private CreateThresholdRequest createRequest;
    private UpdateThresholdRequest updateRequest;
    private UUID thresholdId;
    private UUID budgetId;
    private UUID userId;
    private Budget budget;
    private User user;

    @BeforeEach
    void setUp() {
        thresholdId = UUID.randomUUID();
        budgetId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .username("manager")
                .email("manager@example.com")
                .firstName("Budget")
                .lastName("Manager")
                .build();

        budget = Budget.builder()
                .id(budgetId)
                .name("Q1 Marketing Budget")
                .period(BudgetPeriod.QUARTERLY)
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 3, 31))
                .allocatedAmount(new BigDecimal("50000.00"))
                .consumedAmount(new BigDecimal("25000.00"))
                .isActive(true)
                .build();

        threshold = BudgetThreshold.builder()
                .id(thresholdId)
                .budget(budget)
                .percentage(new BigDecimal("80.0"))
                .alertEnabled(true)
                .build();
        threshold.addNotificationRecipient(user);

        thresholdDto = BudgetThresholdDto.builder()
                .id(thresholdId)
                .budgetId(budgetId)
                .percentage(new BigDecimal("80.0"))
                .alertEnabled(true)
                .notificationRecipientIds(Collections.singletonList(userId))
                .build();

        createRequest = CreateThresholdRequest.builder()
                .budgetId(budgetId)
                .percentage(new BigDecimal("80.0"))
                .alertEnabled(true)
                .notificationRecipientIds(Collections.singletonList(userId))
                .build();

        updateRequest = UpdateThresholdRequest.builder()
                .percentage(new BigDecimal("90.0"))
                .alertEnabled(false)
                .build();
    }

    @Test
    void getAllThresholds_ShouldReturnAllThresholds() {
        // Arrange
        List<BudgetThreshold> thresholds = Arrays.asList(threshold);

        when(thresholdRepository.findAll()).thenReturn(thresholds);
        when(budgetMapper.toDto(threshold)).thenReturn(thresholdDto);

        // Act
        List<BudgetThresholdDto> result = thresholdService.getAllThresholds();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPercentage()).isEqualByComparingTo(new BigDecimal("80.0"));
        verify(thresholdRepository, times(1)).findAll();
        verify(budgetMapper, times(1)).toDto(threshold);
    }

    @Test
    void getThresholdById_WhenExists_ShouldReturnThreshold() {
        // Arrange
        when(thresholdRepository.findById(thresholdId)).thenReturn(Optional.of(threshold));
        when(budgetMapper.toDto(threshold)).thenReturn(thresholdDto);

        // Act
        BudgetThresholdDto result = thresholdService.getThresholdById(thresholdId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(thresholdId);
        assertThat(result.getPercentage()).isEqualByComparingTo(new BigDecimal("80.0"));
        verify(thresholdRepository, times(1)).findById(thresholdId);
    }

    @Test
    void getThresholdById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(thresholdRepository.findById(thresholdId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> thresholdService.getThresholdById(thresholdId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Threshold not found with id");

        verify(thresholdRepository, times(1)).findById(thresholdId);
        verify(budgetMapper, never()).toDto(any(BudgetThreshold.class));
    }

    @Test
    void getThresholdsByBudget_ShouldReturnThresholdsForBudget() {
        // Arrange
        List<BudgetThreshold> thresholds = Arrays.asList(threshold);

        when(thresholdRepository.findByBudgetId(budgetId)).thenReturn(thresholds);
        when(budgetMapper.toDto(threshold)).thenReturn(thresholdDto);

        // Act
        List<BudgetThresholdDto> result = thresholdService.getThresholdsByBudget(budgetId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBudgetId()).isEqualTo(budgetId);
        verify(thresholdRepository, times(1)).findByBudgetId(budgetId);
    }

    @Test
    void getEnabledThresholds_ShouldReturnOnlyEnabledThresholds() {
        // Arrange
        List<BudgetThreshold> thresholds = Arrays.asList(threshold);

        when(thresholdRepository.findByAlertEnabled(true)).thenReturn(thresholds);
        when(budgetMapper.toDto(threshold)).thenReturn(thresholdDto);

        // Act
        List<BudgetThresholdDto> result = thresholdService.getEnabledThresholds();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAlertEnabled()).isTrue();
        verify(thresholdRepository, times(1)).findByAlertEnabled(true);
    }

    @Test
    void createThreshold_WithValidData_ShouldCreateSuccessfully() {
        // Arrange
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(thresholdRepository.existsByBudgetIdAndPercentage(budgetId, new BigDecimal("80.0"))).thenReturn(false);
        when(thresholdRepository.save(any(BudgetThreshold.class))).thenReturn(threshold);
        when(budgetMapper.toDto(threshold)).thenReturn(thresholdDto);

        // Act
        BudgetThresholdDto result = thresholdService.createThreshold(createRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPercentage()).isEqualByComparingTo(new BigDecimal("80.0"));
        verify(budgetRepository, times(1)).findById(budgetId);
        verify(userRepository, times(1)).findById(userId);
        verify(thresholdRepository, times(1)).save(any(BudgetThreshold.class));
    }

    @Test
    void createThreshold_WithDuplicatePercentage_ShouldThrowException() {
        // Arrange
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(thresholdRepository.existsByBudgetIdAndPercentage(budgetId, new BigDecimal("80.0"))).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> thresholdService.createThreshold(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(thresholdRepository, never()).save(any());
    }

    @Test
    void createThreshold_WithInvalidPercentage_ShouldThrowException() {
        // Arrange
        CreateThresholdRequest invalidRequest = CreateThresholdRequest.builder()
                .budgetId(budgetId)
                .percentage(new BigDecimal("150.0"))
                .alertEnabled(true)
                .notificationRecipientIds(Collections.singletonList(userId))
                .build();

        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        // Act & Assert
        assertThatThrownBy(() -> thresholdService.createThreshold(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Percentage must be between 0 and 100");

        verify(thresholdRepository, never()).save(any());
    }

    @Test
    void updateThreshold_WithValidData_ShouldUpdateSuccessfully() {
        // Arrange
        when(thresholdRepository.findById(thresholdId)).thenReturn(Optional.of(threshold));
        when(thresholdRepository.save(any(BudgetThreshold.class))).thenReturn(threshold);
        when(budgetMapper.toDto(threshold)).thenReturn(thresholdDto);

        // Act
        BudgetThresholdDto result = thresholdService.updateThreshold(thresholdId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        verify(thresholdRepository, times(1)).findById(thresholdId);
        verify(thresholdRepository, times(1)).save(any(BudgetThreshold.class));
    }

    @Test
    void deleteThreshold_WhenExists_ShouldDelete() {
        // Arrange
        when(thresholdRepository.findById(thresholdId)).thenReturn(Optional.of(threshold));

        // Act
        thresholdService.deleteThreshold(thresholdId);

        // Assert
        verify(thresholdRepository, times(1)).findById(thresholdId);
        verify(thresholdRepository, times(1)).delete(threshold);
    }

    @Test
    void enableThreshold_WhenExists_ShouldEnable() {
        // Arrange
        threshold.setAlertEnabled(false);
        when(thresholdRepository.findById(thresholdId)).thenReturn(Optional.of(threshold));
        when(thresholdRepository.save(threshold)).thenReturn(threshold);
        when(budgetMapper.toDto(threshold)).thenReturn(thresholdDto);

        // Act
        BudgetThresholdDto result = thresholdService.enableThreshold(thresholdId);

        // Assert
        assertThat(threshold.getAlertEnabled()).isTrue();
        verify(thresholdRepository, times(1)).save(threshold);
    }

    @Test
    void disableThreshold_WhenExists_ShouldDisable() {
        // Arrange
        when(thresholdRepository.findById(thresholdId)).thenReturn(Optional.of(threshold));
        when(thresholdRepository.save(threshold)).thenReturn(threshold);
        when(budgetMapper.toDto(threshold)).thenReturn(thresholdDto);

        // Act
        BudgetThresholdDto result = thresholdService.disableThreshold(thresholdId);

        // Assert
        assertThat(threshold.getAlertEnabled()).isFalse();
        verify(thresholdRepository, times(1)).save(threshold);
    }

    @Test
    void addNotificationRecipient_ShouldAddRecipient() {
        // Arrange
        UUID newUserId = UUID.randomUUID();
        User newUser = User.builder()
                .id(newUserId)
                .username("newuser")
                .email("newuser@example.com")
                .build();

        when(thresholdRepository.findById(thresholdId)).thenReturn(Optional.of(threshold));
        when(userRepository.findById(newUserId)).thenReturn(Optional.of(newUser));
        when(thresholdRepository.save(threshold)).thenReturn(threshold);
        when(budgetMapper.toDto(threshold)).thenReturn(thresholdDto);

        // Act
        BudgetThresholdDto result = thresholdService.addNotificationRecipient(thresholdId, newUserId);

        // Assert
        assertThat(threshold.getNotificationRecipients()).hasSize(2);
        verify(thresholdRepository, times(1)).save(threshold);
    }

    @Test
    void removeNotificationRecipient_ShouldRemoveRecipient() {
        // Arrange
        when(thresholdRepository.findById(thresholdId)).thenReturn(Optional.of(threshold));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(thresholdRepository.save(threshold)).thenReturn(threshold);
        when(budgetMapper.toDto(threshold)).thenReturn(thresholdDto);

        // Act
        BudgetThresholdDto result = thresholdService.removeNotificationRecipient(thresholdId, userId);

        // Assert
        assertThat(threshold.getNotificationRecipients()).isEmpty();
        verify(thresholdRepository, times(1)).save(threshold);
    }

    @Test
    void checkThresholdBreached_WhenBreached_ShouldReturnTrue() {
        // Arrange
        budget.setConsumedAmount(new BigDecimal("41000.00")); // 82% of 50000
        when(thresholdRepository.findById(thresholdId)).thenReturn(Optional.of(threshold));

        // Act
        boolean breached = thresholdService.checkThresholdBreached(thresholdId);

        // Assert
        assertThat(breached).isTrue();
    }

    @Test
    void checkThresholdBreached_WhenNotBreached_ShouldReturnFalse() {
        // Arrange
        budget.setConsumedAmount(new BigDecimal("30000.00")); // 60% of 50000
        when(thresholdRepository.findById(thresholdId)).thenReturn(Optional.of(threshold));

        // Act
        boolean breached = thresholdService.checkThresholdBreached(thresholdId);

        // Assert
        assertThat(breached).isFalse();
    }
}
