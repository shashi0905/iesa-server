package com.technogise.iesa.budgetmanagement.dto;

import com.technogise.iesa.budgetmanagement.domain.Budget;
import com.technogise.iesa.budgetmanagement.domain.BudgetAlert;
import com.technogise.iesa.budgetmanagement.domain.BudgetThreshold;
import com.technogise.iesa.usermanagement.domain.User;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(target = "segmentId", source = "segment.id")
    @Mapping(target = "segmentName", source = "segment.name")
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "period", expression = "java(budget.getPeriod().name())")
    @Mapping(target = "remainingAmount", expression = "java(budget.getRemainingAmount())")
    @Mapping(target = "utilizationPercentage", expression = "java(budget.getUtilizationPercentage())")
    BudgetDto toDto(Budget budget);

    List<BudgetDto> toDtoList(List<Budget> budgets);

    @Mapping(target = "budgetId", source = "budget.id")
    @Mapping(target = "notificationRecipientIds", expression = "java(mapUsersToIds(threshold.getNotificationRecipients()))")
    BudgetThresholdDto toDto(BudgetThreshold threshold);

    List<BudgetThresholdDto> toThresholdDtoList(List<BudgetThreshold> thresholds);

    @Mapping(target = "budgetId", source = "budget.id")
    @Mapping(target = "budgetName", source = "budget.name")
    @Mapping(target = "thresholdId", source = "threshold.id")
    @Mapping(target = "thresholdPercentage", source = "threshold.percentage")
    BudgetAlertDto toDto(BudgetAlert alert);

    List<BudgetAlertDto> toAlertDtoList(List<BudgetAlert> alerts);

    default List<UUID> mapUsersToIds(Set<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}
