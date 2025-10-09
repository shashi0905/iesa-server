package com.company.iesa.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error response structure
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> errors; // For validation errors

}
