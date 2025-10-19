package com.technogise.iesa.budgetmanagement.service;

import com.technogise.iesa.budgetmanagement.domain.Budget;
import com.technogise.iesa.budgetmanagement.domain.BudgetPeriod;
import com.technogise.iesa.budgetmanagement.dto.BudgetDto;
import com.technogise.iesa.budgetmanagement.dto.BudgetMapper;
import com.technogise.iesa.budgetmanagement.dto.CreateBudgetRequest;
import com.technogise.iesa.budgetmanagement.dto.UpdateBudgetRequest;
import com.technogise.iesa.budgetmanagement.repository.BudgetRepository;
import com.technogise.iesa.usermanagement.domain.Department;
import com.technogise.iesa.usermanagement.repository.DepartmentRepository;
import com.technogise.iesa.segmentmanagement.domain.Segment;
import com.technogise.iesa.segmentmanagement.repository.SegmentRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final SegmentRepository segmentRepository;
    private final DepartmentRepository departmentRepository;
    private final BudgetMapper budgetMapper;

    @Transactional(readOnly = true)
    public List<BudgetDto> getAllBudgets() {
        return budgetMapper.toDtoList(budgetRepository.findAllNotDeleted());
    }

    @Transactional(readOnly = true)
    public List<BudgetDto> getAllActiveBudgets() {
        return budgetMapper.toDtoList(budgetRepository.findAllActive());
    }

    @Transactional(readOnly = true)
    public List<BudgetDto> getBudgetsBySegment(UUID segmentId) {
        return budgetMapper.toDtoList(budgetRepository.findBySegmentId(segmentId));
    }

    @Transactional(readOnly = true)
    public List<BudgetDto> getBudgetsByDepartment(UUID departmentId) {
        return budgetMapper.toDtoList(budgetRepository.findByDepartmentId(departmentId));
    }

    @Transactional(readOnly = true)
    public List<BudgetDto> getCurrentBudgets() {
        return budgetMapper.toDtoList(budgetRepository.findCurrentBudgets(LocalDate.now()));
    }

    @Transactional(readOnly = true)
    public List<BudgetDto> getActiveBudgetsBySegment(UUID segmentId) {
        List<Budget> budgets = budgetRepository.findActiveBySegmentAndDate(segmentId, LocalDate.now());
        return budgetMapper.toDtoList(budgets);
    }

    @Transactional(readOnly = true)
    public List<BudgetDto> getActiveBudgetsByDepartment(UUID departmentId) {
        List<Budget> budgets = budgetRepository.findActiveByDepartmentAndDate(departmentId, LocalDate.now());
        return budgetMapper.toDtoList(budgets);
    }

    @Transactional(readOnly = true)
    public BudgetDto getBudgetById(UUID id) {
        Budget budget = findBudgetById(id);
        return budgetMapper.toDto(budget);
    }

    @Transactional(readOnly = true)
    public List<BudgetDto> getBudgetsByPeriod(BudgetPeriod period) {
        return budgetMapper.toDtoList(budgetRepository.findByPeriod(period));
    }

    @Transactional(readOnly = true)
    public List<BudgetDto> getBudgetsByDateRange(LocalDate startDate, LocalDate endDate) {
        return budgetMapper.toDtoList(budgetRepository.findByStartDateBetween(startDate, endDate));
    }

    public BudgetDto createBudget(CreateBudgetRequest request) {
        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate()) ||
            request.getEndDate().isEqual(request.getStartDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        // Check for duplicate budget
        if (budgetRepository.existsByNameAndPeriod(
                request.getName(),
                BudgetPeriod.valueOf(request.getPeriod()),
                request.getStartDate(),
                request.getEndDate())) {
            throw new IllegalArgumentException("Budget with same name and period already exists");
        }

        Budget budget = Budget.builder()
                .name(request.getName())
                .period(BudgetPeriod.valueOf(request.getPeriod()))
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .allocatedAmount(request.getAllocatedAmount())
                .consumedAmount(BigDecimal.ZERO)
                .isActive(true)
                .build();

        // Set segment if provided
        if (request.getSegmentId() != null) {
            Segment segment = segmentRepository.findById(request.getSegmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Segment not found with id: " + request.getSegmentId()));
            budget.setSegment(segment);
        }

        // Set department if provided
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));
            budget.setDepartment(department);
        }

        Budget savedBudget = budgetRepository.save(budget);
        return budgetMapper.toDto(savedBudget);
    }

    public BudgetDto updateBudget(UUID id, UpdateBudgetRequest request) {
        Budget budget = findBudgetById(id);

        if (request.getName() != null) {
            budget.setName(request.getName());
        }
        if (request.getStartDate() != null) {
            budget.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            budget.setEndDate(request.getEndDate());
        }
        if (request.getAllocatedAmount() != null) {
            budget.setAllocatedAmount(request.getAllocatedAmount());
        }

        // Validate dates if both are set
        if (budget.getEndDate().isBefore(budget.getStartDate()) ||
            budget.getEndDate().isEqual(budget.getStartDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        Budget updatedBudget = budgetRepository.save(budget);
        return budgetMapper.toDto(updatedBudget);
    }

    public void deleteBudget(UUID id) {
        Budget budget = findBudgetById(id);
        budget.setDeletedAt(Instant.now());
        budgetRepository.save(budget);
    }

    public BudgetDto activateBudget(UUID id) {
        Budget budget = findBudgetById(id);
        budget.setIsActive(true);
        Budget updatedBudget = budgetRepository.save(budget);
        return budgetMapper.toDto(updatedBudget);
    }

    public BudgetDto deactivateBudget(UUID id) {
        Budget budget = findBudgetById(id);
        budget.setIsActive(false);
        Budget updatedBudget = budgetRepository.save(budget);
        return budgetMapper.toDto(updatedBudget);
    }

    public BudgetDto updateConsumption(UUID id, BigDecimal amount) {
        Budget budget = findBudgetById(id);
        budget.addConsumption(amount);
        Budget updatedBudget = budgetRepository.save(budget);
        return budgetMapper.toDto(updatedBudget);
    }

    @Transactional(readOnly = true)
    public BigDecimal getRemainingAmount(UUID id) {
        Budget budget = findBudgetById(id);
        return budget.getRemainingAmount();
    }

    @Transactional(readOnly = true)
    public BigDecimal getBudgetUtilization(UUID id) {
        Budget budget = findBudgetById(id);
        return budget.getUtilizationPercentage();
    }

    @Transactional(readOnly = true)
    public boolean checkBudgetAvailability(UUID id, BigDecimal requestedAmount) {
        Budget budget = findBudgetById(id);
        return budget.getRemainingAmount().compareTo(requestedAmount) >= 0;
    }

    private Budget findBudgetById(UUID id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
    }
}
