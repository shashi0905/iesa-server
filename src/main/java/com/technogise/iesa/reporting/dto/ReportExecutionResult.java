package com.technogise.iesa.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportExecutionResult {
    private List<Map<String, Object>> data;
    private Integer totalRecords;
    private Long executionTime;
}
