package com.technogise.iesa.budgetmanagement.service;

import com.technogise.iesa.budgetmanagement.domain.Budget;
import com.technogise.iesa.budgetmanagement.domain.BudgetAlert;
import com.technogise.iesa.budgetmanagement.domain.BudgetThreshold;
import com.technogise.iesa.budgetmanagement.dto.BudgetAlertDto;
import com.technogise.iesa.budgetmanagement.dto.BudgetMapper;
import com.technogise.iesa.budgetmanagement.repository.BudgetAlertRepository;
import com.technogise.iesa.budgetmanagement.repository.BudgetThresholdRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetAlertService {

    private final BudgetAlertRepository alertRepository;
    private final BudgetThresholdRepository thresholdRepository;
    private final BudgetMapper budgetMapper;

    @Transactional(readOnly = true)
    public List<BudgetAlertDto> getAllAlerts() {
        return budgetMapper.toAlertDtoList(alertRepository.findAll());
    }

    @Transactional(readOnly = true)
    public BudgetAlertDto getAlertById(UUID id) {
        BudgetAlert alert = findAlertById(id);
        return budgetMapper.toDto(alert);
    }

    @Transactional(readOnly = true)
    public List<BudgetAlertDto> getAlertsByBudget(UUID budgetId) {
        return budgetMapper.toAlertDtoList(alertRepository.findByBudgetId(budgetId));
    }

    @Transactional(readOnly = true)
    public List<BudgetAlertDto> getUnacknowledgedAlerts() {
        return budgetMapper.toAlertDtoList(alertRepository.findByIsAcknowledged(false));
    }

    @Transactional(readOnly = true)
    public List<BudgetAlertDto> getRecentAlerts(int days) {
        Instant since = Instant.now().minus(days, ChronoUnit.DAYS);
        return budgetMapper.toAlertDtoList(alertRepository.findRecentAlerts(since));
    }

    public BudgetAlertDto createAlert(UUID thresholdId, String message) {
        BudgetThreshold threshold = thresholdRepository.findById(thresholdId)
                .orElseThrow(() -> new ResourceNotFoundException("Threshold not found with id: " + thresholdId));

        BudgetAlert alert = BudgetAlert.builder()
                .budget(threshold.getBudget())
                .threshold(threshold)
                .triggeredDate(Instant.now())
                .message(message)
                .isAcknowledged(false)
                .build();

        BudgetAlert savedAlert = alertRepository.save(alert);
        return budgetMapper.toDto(savedAlert);
    }

    public BudgetAlertDto acknowledgeAlert(UUID id) {
        BudgetAlert alert = findAlertById(id);
        alert.acknowledge();
        BudgetAlert updatedAlert = alertRepository.save(alert);
        return budgetMapper.toDto(updatedAlert);
    }

    public void deleteAlert(UUID id) {
        BudgetAlert alert = findAlertById(id);
        alertRepository.delete(alert);
    }

    public int deleteAcknowledgedAlerts() {
        return alertRepository.deleteByIsAcknowledged(true);
    }

    public int deleteOldAlerts(int days) {
        Instant before = Instant.now().minus(days, ChronoUnit.DAYS);
        return alertRepository.deleteOldAlerts(before);
    }

    public int checkAndCreateAlerts() {
        List<BudgetThreshold> enabledThresholds = thresholdRepository.findByAlertEnabled(true);
        int alertsCreated = 0;

        for (BudgetThreshold threshold : enabledThresholds) {
            Budget budget = threshold.getBudget();

            // Check if threshold is breached
            if (threshold.isBreached()) {
                // Check if alert already exists for this budget and threshold
                boolean alertExists = alertRepository.existsByBudgetIdAndThresholdIdAndIsAcknowledged(
                        budget.getId(),
                        threshold.getId(),
                        false
                );

                if (!alertExists) {
                    // Create new alert
                    String message = String.format(
                            "Budget threshold of %s%% has been reached",
                            threshold.getPercentage()
                    );

                    BudgetAlert alert = BudgetAlert.builder()
                            .budget(budget)
                            .threshold(threshold)
                            .triggeredDate(Instant.now())
                            .message(message)
                            .isAcknowledged(false)
                            .build();

                    alertRepository.save(alert);
                    alertsCreated++;
                }
            }
        }

        return alertsCreated;
    }

    private BudgetAlert findAlertById(UUID id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + id));
    }
}
