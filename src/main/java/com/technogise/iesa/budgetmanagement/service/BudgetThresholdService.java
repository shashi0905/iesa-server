package com.technogise.iesa.budgetmanagement.service;

import com.technogise.iesa.budgetmanagement.domain.Budget;
import com.technogise.iesa.budgetmanagement.domain.BudgetThreshold;
import com.technogise.iesa.budgetmanagement.dto.BudgetMapper;
import com.technogise.iesa.budgetmanagement.dto.BudgetThresholdDto;
import com.technogise.iesa.budgetmanagement.dto.CreateThresholdRequest;
import com.technogise.iesa.budgetmanagement.dto.UpdateThresholdRequest;
import com.technogise.iesa.budgetmanagement.repository.BudgetRepository;
import com.technogise.iesa.budgetmanagement.repository.BudgetThresholdRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import com.technogise.iesa.usermanagement.domain.User;
import com.technogise.iesa.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetThresholdService {

    private final BudgetThresholdRepository thresholdRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final BudgetMapper budgetMapper;

    @Transactional(readOnly = true)
    public List<BudgetThresholdDto> getAllThresholds() {
        return budgetMapper.toThresholdDtoList(thresholdRepository.findAll());
    }

    @Transactional(readOnly = true)
    public BudgetThresholdDto getThresholdById(UUID id) {
        BudgetThreshold threshold = findThresholdById(id);
        return budgetMapper.toDto(threshold);
    }

    @Transactional(readOnly = true)
    public List<BudgetThresholdDto> getThresholdsByBudget(UUID budgetId) {
        return budgetMapper.toThresholdDtoList(thresholdRepository.findByBudgetId(budgetId));
    }

    @Transactional(readOnly = true)
    public List<BudgetThresholdDto> getEnabledThresholds() {
        return budgetMapper.toThresholdDtoList(thresholdRepository.findByAlertEnabled(true));
    }

    public BudgetThresholdDto createThreshold(CreateThresholdRequest request) {
        // Validate percentage range
        if (request.getPercentage().compareTo(BigDecimal.ZERO) < 0 ||
            request.getPercentage().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }

        // Find budget
        Budget budget = budgetRepository.findById(request.getBudgetId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + request.getBudgetId()));

        // Check for duplicate threshold percentage for this budget
        if (thresholdRepository.existsByBudgetIdAndPercentage(request.getBudgetId(), request.getPercentage())) {
            throw new IllegalArgumentException("Threshold with this percentage already exists for this budget");
        }

        BudgetThreshold threshold = BudgetThreshold.builder()
                .budget(budget)
                .percentage(request.getPercentage())
                .alertEnabled(request.getAlertEnabled())
                .build();

        // Add notification recipients if provided
        if (request.getNotificationRecipientIds() != null && !request.getNotificationRecipientIds().isEmpty()) {
            for (UUID userId : request.getNotificationRecipientIds()) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                threshold.addNotificationRecipient(user);
            }
        }

        BudgetThreshold savedThreshold = thresholdRepository.save(threshold);
        return budgetMapper.toDto(savedThreshold);
    }

    public BudgetThresholdDto updateThreshold(UUID id, UpdateThresholdRequest request) {
        BudgetThreshold threshold = findThresholdById(id);

        if (request.getPercentage() != null) {
            // Validate percentage range
            if (request.getPercentage().compareTo(BigDecimal.ZERO) < 0 ||
                request.getPercentage().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Percentage must be between 0 and 100");
            }
            threshold.setPercentage(request.getPercentage());
        }

        if (request.getAlertEnabled() != null) {
            threshold.setAlertEnabled(request.getAlertEnabled());
        }

        BudgetThreshold updatedThreshold = thresholdRepository.save(threshold);
        return budgetMapper.toDto(updatedThreshold);
    }

    public void deleteThreshold(UUID id) {
        BudgetThreshold threshold = findThresholdById(id);
        thresholdRepository.delete(threshold);
    }

    public BudgetThresholdDto enableThreshold(UUID id) {
        BudgetThreshold threshold = findThresholdById(id);
        threshold.setAlertEnabled(true);
        BudgetThreshold updatedThreshold = thresholdRepository.save(threshold);
        return budgetMapper.toDto(updatedThreshold);
    }

    public BudgetThresholdDto disableThreshold(UUID id) {
        BudgetThreshold threshold = findThresholdById(id);
        threshold.setAlertEnabled(false);
        BudgetThreshold updatedThreshold = thresholdRepository.save(threshold);
        return budgetMapper.toDto(updatedThreshold);
    }

    public BudgetThresholdDto addNotificationRecipient(UUID thresholdId, UUID userId) {
        BudgetThreshold threshold = findThresholdById(thresholdId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        threshold.addNotificationRecipient(user);
        BudgetThreshold updatedThreshold = thresholdRepository.save(threshold);
        return budgetMapper.toDto(updatedThreshold);
    }

    public BudgetThresholdDto removeNotificationRecipient(UUID thresholdId, UUID userId) {
        BudgetThreshold threshold = findThresholdById(thresholdId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        threshold.removeNotificationRecipient(user);
        BudgetThreshold updatedThreshold = thresholdRepository.save(threshold);
        return budgetMapper.toDto(updatedThreshold);
    }

    @Transactional(readOnly = true)
    public boolean checkThresholdBreached(UUID id) {
        BudgetThreshold threshold = findThresholdById(id);
        return threshold.isBreached();
    }

    private BudgetThreshold findThresholdById(UUID id) {
        return thresholdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Threshold not found with id: " + id));
    }
}
