package com.technogise.iesa.reporting.service;

import com.technogise.iesa.reporting.domain.AnalyticsSnapshot;
import com.technogise.iesa.reporting.domain.DimensionType;
import com.technogise.iesa.reporting.dto.AnalyticsSnapshotDto;
import com.technogise.iesa.reporting.dto.AnalyticsSnapshotMapper;
import com.technogise.iesa.reporting.repository.AnalyticsSnapshotRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsSnapshotService {

    private final AnalyticsSnapshotRepository snapshotRepository;
    private final AnalyticsSnapshotMapper snapshotMapper;

    @Transactional(readOnly = true)
    public List<AnalyticsSnapshotDto> getSnapshotsByDateAndDimension(LocalDate date, DimensionType dimension) {
        return snapshotMapper.toDtoList(snapshotRepository.findBySnapshotDateAndDimension(date, dimension));
    }

    @Transactional(readOnly = true)
    public List<AnalyticsSnapshotDto> getSnapshotsByDimensionAndDateRange(
            DimensionType dimension,
            LocalDate startDate,
            LocalDate endDate) {
        return snapshotMapper.toDtoList(
                snapshotRepository.findByDimensionAndDateRange(dimension, startDate, endDate));
    }

    @Transactional(readOnly = true)
    public List<AnalyticsSnapshotDto> getLatestSnapshotsByDimension(DimensionType dimension) {
        return snapshotMapper.toDtoList(snapshotRepository.findLatestByDimension(dimension));
    }

    @Transactional(readOnly = true)
    public AnalyticsSnapshotDto getSnapshotById(UUID id) {
        AnalyticsSnapshot snapshot = snapshotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analytics snapshot not found with id: " + id));
        return snapshotMapper.toDto(snapshot);
    }

    public AnalyticsSnapshotDto createOrUpdateSnapshot(
            LocalDate snapshotDate,
            DimensionType dimension,
            String dimensionValue,
            BigDecimal totalExpenses,
            Integer expenseCount,
            Integer approvedCount,
            Integer pendingCount,
            Integer rejectedCount,
            BigDecimal totalBudgetAllocated,
            BigDecimal totalBudgetConsumed,
            Map<String, Object> metadata) {

        AnalyticsSnapshot snapshot = snapshotRepository
                .findBySnapshotDateAndDimensionAndValue(snapshotDate, dimension, dimensionValue)
                .orElse(AnalyticsSnapshot.builder()
                        .snapshotDate(snapshotDate)
                        .dimension(dimension)
                        .dimensionValue(dimensionValue)
                        .build());

        snapshot.setTotalExpenses(totalExpenses != null ? totalExpenses : BigDecimal.ZERO);
        snapshot.setExpenseCount(expenseCount != null ? expenseCount : 0);
        snapshot.setApprovedCount(approvedCount != null ? approvedCount : 0);
        snapshot.setPendingCount(pendingCount != null ? pendingCount : 0);
        snapshot.setRejectedCount(rejectedCount != null ? rejectedCount : 0);
        snapshot.setTotalBudgetAllocated(totalBudgetAllocated);
        snapshot.setTotalBudgetConsumed(totalBudgetConsumed);
        snapshot.setMetadata(metadata);

        AnalyticsSnapshot savedSnapshot = snapshotRepository.save(snapshot);
        return snapshotMapper.toDto(savedSnapshot);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalExpensesByDimension(DimensionType dimension, LocalDate startDate, LocalDate endDate) {
        List<AnalyticsSnapshot> snapshots = snapshotRepository.findByDimensionAndDateRange(dimension, startDate, endDate);
        return snapshots.stream()
                .map(AnalyticsSnapshot::getTotalExpenses)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public Integer getTotalExpenseCountByDimension(DimensionType dimension, LocalDate startDate, LocalDate endDate) {
        List<AnalyticsSnapshot> snapshots = snapshotRepository.findByDimensionAndDateRange(dimension, startDate, endDate);
        return snapshots.stream()
                .mapToInt(AnalyticsSnapshot::getExpenseCount)
                .sum();
    }
}
