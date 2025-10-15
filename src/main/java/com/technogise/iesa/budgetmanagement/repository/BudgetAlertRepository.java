package com.technogise.iesa.budgetmanagement.repository;

import com.technogise.iesa.budgetmanagement.domain.BudgetAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetAlertRepository extends JpaRepository<BudgetAlert, UUID> {

    @Query("SELECT a FROM BudgetAlert a WHERE a.budget.id = :budgetId ORDER BY a.triggeredDate DESC")
    List<BudgetAlert> findByBudgetId(@Param("budgetId") UUID budgetId);

    List<BudgetAlert> findByIsAcknowledged(Boolean isAcknowledged);

    @Query("SELECT a FROM BudgetAlert a WHERE a.triggeredDate >= :since ORDER BY a.triggeredDate DESC")
    List<BudgetAlert> findRecentAlerts(@Param("since") Instant since);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM BudgetAlert a WHERE a.budget.id = :budgetId AND a.threshold.id = :thresholdId AND a.isAcknowledged = :isAcknowledged")
    boolean existsByBudgetIdAndThresholdIdAndIsAcknowledged(
        @Param("budgetId") UUID budgetId,
        @Param("thresholdId") UUID thresholdId,
        @Param("isAcknowledged") Boolean isAcknowledged
    );

    @Modifying
    @Query("DELETE FROM BudgetAlert a WHERE a.isAcknowledged = :isAcknowledged")
    int deleteByIsAcknowledged(@Param("isAcknowledged") Boolean isAcknowledged);

    @Modifying
    @Query("DELETE FROM BudgetAlert a WHERE a.triggeredDate < :before")
    int deleteOldAlerts(@Param("before") Instant before);
}
