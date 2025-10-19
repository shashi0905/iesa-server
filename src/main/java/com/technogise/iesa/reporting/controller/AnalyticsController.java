package com.technogise.iesa.reporting.controller;

import com.technogise.iesa.reporting.domain.DimensionType;
import com.technogise.iesa.reporting.dto.AnalyticsSnapshotDto;
import com.technogise.iesa.reporting.service.AnalyticsSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsSnapshotService analyticsService;

    @GetMapping("/snapshots/{id}")
    public ResponseEntity<AnalyticsSnapshotDto> getSnapshotById(@PathVariable UUID id) {
        return ResponseEntity.ok(analyticsService.getSnapshotById(id));
    }

    @GetMapping("/snapshots")
    public ResponseEntity<List<AnalyticsSnapshotDto>> getSnapshotsByDateAndDimension(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam DimensionType dimension) {
        return ResponseEntity.ok(analyticsService.getSnapshotsByDateAndDimension(date, dimension));
    }

    @GetMapping("/snapshots/range")
    public ResponseEntity<List<AnalyticsSnapshotDto>> getSnapshotsByDimensionAndDateRange(
            @RequestParam DimensionType dimension,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getSnapshotsByDimensionAndDateRange(dimension, startDate, endDate));
    }

    @GetMapping("/snapshots/latest")
    public ResponseEntity<List<AnalyticsSnapshotDto>> getLatestSnapshotsByDimension(
            @RequestParam DimensionType dimension) {
        return ResponseEntity.ok(analyticsService.getLatestSnapshotsByDimension(dimension));
    }

    @GetMapping("/total-expenses")
    public ResponseEntity<BigDecimal> getTotalExpensesByDimension(
            @RequestParam DimensionType dimension,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getTotalExpensesByDimension(dimension, startDate, endDate));
    }

    @GetMapping("/expense-count")
    public ResponseEntity<Integer> getTotalExpenseCountByDimension(
            @RequestParam DimensionType dimension,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getTotalExpenseCountByDimension(dimension, startDate, endDate));
    }
}
