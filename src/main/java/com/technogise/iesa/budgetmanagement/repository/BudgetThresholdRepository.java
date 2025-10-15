package com.technogise.iesa.budgetmanagement.repository;

import com.technogise.iesa.budgetmanagement.domain.BudgetThreshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetThresholdRepository extends JpaRepository<BudgetThreshold, UUID> {

    List<BudgetThreshold> findByBudgetId(UUID budgetId);

    List<BudgetThreshold> findByAlertEnabled(Boolean alertEnabled);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM BudgetThreshold t WHERE t.budget.id = :budgetId AND t.percentage = :percentage")
    boolean existsByBudgetIdAndPercentage(@Param("budgetId") UUID budgetId, @Param("percentage") BigDecimal percentage);
}
