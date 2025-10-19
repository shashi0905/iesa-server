package com.technogise.iesa.budgetmanagement.service;

import com.technogise.iesa.budgetmanagement.domain.Budget;
import com.technogise.iesa.budgetmanagement.domain.BudgetAlert;
import com.technogise.iesa.budgetmanagement.domain.BudgetPeriod;
import com.technogise.iesa.budgetmanagement.domain.BudgetThreshold;
import com.technogise.iesa.budgetmanagement.dto.BudgetAlertDto;
import com.technogise.iesa.budgetmanagement.dto.BudgetMapper;
import com.technogise.iesa.budgetmanagement.repository.BudgetAlertRepository;
import com.technogise.iesa.budgetmanagement.repository.BudgetThresholdRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
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
class BudgetAlertServiceTest {

    @Mock
    private BudgetAlertRepository alertRepository;

    @Mock
    private BudgetThresholdRepository thresholdRepository;

    @Mock
    private BudgetMapper budgetMapper;

    @InjectMocks
    private BudgetAlertService alertService;

    private BudgetAlert alert;
    private BudgetAlertDto alertDto;
    private UUID alertId;
    private UUID budgetId;
    private UUID thresholdId;
    private Budget budget;
    private BudgetThreshold threshold;

    @BeforeEach
    void setUp() {
        alertId = UUID.randomUUID();
        budgetId = UUID.randomUUID();
        thresholdId = UUID.randomUUID();

        budget = Budget.builder()
                .id(budgetId)
                .name("Q1 Marketing Budget")
                .period(BudgetPeriod.QUARTERLY)
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 3, 31))
                .allocatedAmount(new BigDecimal("50000.00"))
                .consumedAmount(new BigDecimal("41000.00"))
                .isActive(true)
                .build();

        threshold = BudgetThreshold.builder()
                .id(thresholdId)
                .budget(budget)
                .percentage(new BigDecimal("80.0"))
                .alertEnabled(true)
                .build();

        alert = BudgetAlert.builder()
                .id(alertId)
                .budget(budget)
                .threshold(threshold)
                .triggeredDate(Instant.now())
                .message("Budget threshold of 80.0% has been reached")
                .isAcknowledged(false)
                .build();

        alertDto = BudgetAlertDto.builder()
                .id(alertId)
                .budgetId(budgetId)
                .budgetName("Q1 Marketing Budget")
                .thresholdId(thresholdId)
                .thresholdPercentage(new BigDecimal("80.0"))
                .triggeredDate(Instant.now())
                .message("Budget threshold of 80.0% has been reached")
                .isAcknowledged(false)
                .build();
    }

    @Test
    void getAllAlerts_ShouldReturnAllAlerts() {
        // Arrange
        List<BudgetAlert> alerts = Arrays.asList(alert);
        List<BudgetAlertDto> alertDtos = Arrays.asList(alertDto);

        when(alertRepository.findAll()).thenReturn(alerts);
        when(budgetMapper.toAlertDtoList(alerts)).thenReturn(alertDtos);

        // Act
        List<BudgetAlertDto> result = alertService.getAllAlerts();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMessage()).contains("80.0%");
        verify(alertRepository, times(1)).findAll();
        verify(budgetMapper, times(1)).toAlertDtoList(alerts);
    }

    @Test
    void getAlertById_WhenExists_ShouldReturnAlert() {
        // Arrange
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(budgetMapper.toDto(alert)).thenReturn(alertDto);

        // Act
        BudgetAlertDto result = alertService.getAlertById(alertId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(alertId);
        verify(alertRepository, times(1)).findById(alertId);
    }

    @Test
    void getAlertById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(alertRepository.findById(alertId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> alertService.getAlertById(alertId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Alert not found with id");

        verify(alertRepository, times(1)).findById(alertId);
        verify(budgetMapper, never()).toDto(any(BudgetAlert.class));
    }

    @Test
    void getAlertsByBudget_ShouldReturnAlertsForBudget() {
        // Arrange
        List<BudgetAlert> alerts = Arrays.asList(alert);
        List<BudgetAlertDto> alertDtos = Arrays.asList(alertDto);

        when(alertRepository.findByBudgetId(budgetId)).thenReturn(alerts);
        when(budgetMapper.toAlertDtoList(alerts)).thenReturn(alertDtos);

        // Act
        List<BudgetAlertDto> result = alertService.getAlertsByBudget(budgetId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBudgetId()).isEqualTo(budgetId);
        verify(alertRepository, times(1)).findByBudgetId(budgetId);
        verify(budgetMapper, times(1)).toAlertDtoList(alerts);
    }

    @Test
    void getUnacknowledgedAlerts_ShouldReturnOnlyUnacknowledgedAlerts() {
        // Arrange
        List<BudgetAlert> alerts = Arrays.asList(alert);
        List<BudgetAlertDto> alertDtos = Arrays.asList(alertDto);

        when(alertRepository.findByIsAcknowledged(false)).thenReturn(alerts);
        when(budgetMapper.toAlertDtoList(alerts)).thenReturn(alertDtos);

        // Act
        List<BudgetAlertDto> result = alertService.getUnacknowledgedAlerts();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsAcknowledged()).isFalse();
        verify(alertRepository, times(1)).findByIsAcknowledged(false);
        verify(budgetMapper, times(1)).toAlertDtoList(alerts);
    }

    @Test
    void getRecentAlerts_ShouldReturnAlertsFromLastNDays() {
        // Arrange
        int days = 7;
        List<BudgetAlert> alerts = Arrays.asList(alert);
        List<BudgetAlertDto> alertDtos = Arrays.asList(alertDto);

        when(alertRepository.findRecentAlerts(any(Instant.class))).thenReturn(alerts);
        when(budgetMapper.toAlertDtoList(alerts)).thenReturn(alertDtos);

        // Act
        List<BudgetAlertDto> result = alertService.getRecentAlerts(days);

        // Assert
        assertThat(result).hasSize(1);
        verify(alertRepository, times(1)).findRecentAlerts(any(Instant.class));
        verify(budgetMapper, times(1)).toAlertDtoList(alerts);
    }

    @Test
    void createAlert_WithValidData_ShouldCreateSuccessfully() {
        // Arrange
        when(thresholdRepository.findById(thresholdId)).thenReturn(Optional.of(threshold));
        when(alertRepository.save(any(BudgetAlert.class))).thenReturn(alert);
        when(budgetMapper.toDto(alert)).thenReturn(alertDto);

        // Act
        BudgetAlertDto result = alertService.createAlert(thresholdId, "Budget threshold of 80.0% has been reached");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).contains("80.0%");
        verify(thresholdRepository, times(1)).findById(thresholdId);
        verify(alertRepository, times(1)).save(any(BudgetAlert.class));
    }

    @Test
    void acknowledgeAlert_WhenExists_ShouldAcknowledge() {
        // Arrange
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(alert)).thenReturn(alert);
        when(budgetMapper.toDto(alert)).thenReturn(alertDto);

        // Act
        BudgetAlertDto result = alertService.acknowledgeAlert(alertId);

        // Assert
        assertThat(alert.getIsAcknowledged()).isTrue();
        assertThat(alert.getAcknowledgedDate()).isNotNull();
        verify(alertRepository, times(1)).findById(alertId);
        verify(alertRepository, times(1)).save(alert);
    }

    @Test
    void acknowledgeAlert_WhenAlreadyAcknowledged_ShouldNotChange() {
        // Arrange
        alert.setIsAcknowledged(true);
        alert.setAcknowledgedDate(Instant.now());
        Instant originalDate = alert.getAcknowledgedDate();

        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(alert)).thenReturn(alert);
        when(budgetMapper.toDto(alert)).thenReturn(alertDto);

        // Act
        BudgetAlertDto result = alertService.acknowledgeAlert(alertId);

        // Assert
        assertThat(alert.getIsAcknowledged()).isTrue();
        assertThat(alert.getAcknowledgedDate()).isEqualTo(originalDate);
        verify(alertRepository, times(1)).save(alert);
    }

    @Test
    void deleteAlert_WhenExists_ShouldDelete() {
        // Arrange
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));

        // Act
        alertService.deleteAlert(alertId);

        // Assert
        verify(alertRepository, times(1)).findById(alertId);
        verify(alertRepository, times(1)).delete(alert);
    }

    @Test
    void deleteAlert_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(alertRepository.findById(alertId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> alertService.deleteAlert(alertId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Alert not found");

        verify(alertRepository, times(1)).findById(alertId);
        verify(alertRepository, never()).delete(any());
    }

    @Test
    void deleteAcknowledgedAlerts_ShouldDeleteOnlyAcknowledged() {
        // Arrange
        when(alertRepository.deleteByIsAcknowledged(true)).thenReturn(5);

        // Act
        int deletedCount = alertService.deleteAcknowledgedAlerts();

        // Assert
        assertThat(deletedCount).isEqualTo(5);
        verify(alertRepository, times(1)).deleteByIsAcknowledged(true);
    }

    @Test
    void deleteOldAlerts_ShouldDeleteAlertsOlderThanDays() {
        // Arrange
        int days = 30;
        when(alertRepository.deleteOldAlerts(any(Instant.class))).thenReturn(10);

        // Act
        int deletedCount = alertService.deleteOldAlerts(days);

        // Assert
        assertThat(deletedCount).isEqualTo(10);
        verify(alertRepository, times(1)).deleteOldAlerts(any(Instant.class));
    }

    @Test
    void checkAndCreateAlerts_WhenThresholdBreached_ShouldCreateAlert() {
        // Arrange
        when(thresholdRepository.findByAlertEnabled(true)).thenReturn(Arrays.asList(threshold));
        when(alertRepository.existsByBudgetIdAndThresholdIdAndIsAcknowledged(budgetId, thresholdId, false))
                .thenReturn(false);
        when(alertRepository.save(any(BudgetAlert.class))).thenReturn(alert);

        // Act
        int alertsCreated = alertService.checkAndCreateAlerts();

        // Assert
        assertThat(alertsCreated).isEqualTo(1);
        verify(thresholdRepository, times(1)).findByAlertEnabled(true);
        verify(alertRepository, times(1)).save(any(BudgetAlert.class));
    }

    @Test
    void checkAndCreateAlerts_WhenThresholdNotBreached_ShouldNotCreateAlert() {
        // Arrange
        budget.setConsumedAmount(new BigDecimal("30000.00")); // 60% - below 80% threshold
        when(thresholdRepository.findByAlertEnabled(true)).thenReturn(Arrays.asList(threshold));

        // Act
        int alertsCreated = alertService.checkAndCreateAlerts();

        // Assert
        assertThat(alertsCreated).isEqualTo(0);
        verify(thresholdRepository, times(1)).findByAlertEnabled(true);
        verify(alertRepository, never()).save(any());
    }

    @Test
    void checkAndCreateAlerts_WhenAlertAlreadyExists_ShouldNotCreateDuplicate() {
        // Arrange
        when(thresholdRepository.findByAlertEnabled(true)).thenReturn(Arrays.asList(threshold));
        when(alertRepository.existsByBudgetIdAndThresholdIdAndIsAcknowledged(budgetId, thresholdId, false))
                .thenReturn(true);

        // Act
        int alertsCreated = alertService.checkAndCreateAlerts();

        // Assert
        assertThat(alertsCreated).isEqualTo(0);
        verify(alertRepository, never()).save(any());
    }
}
