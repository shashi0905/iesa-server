package com.technogise.iesa.segmentmanagement.controller;

import com.technogise.iesa.segmentmanagement.domain.SegmentType;
import com.technogise.iesa.segmentmanagement.dto.CreateSegmentRequest;
import com.technogise.iesa.segmentmanagement.dto.SegmentDto;
import com.technogise.iesa.segmentmanagement.dto.UpdateSegmentRequest;
import com.technogise.iesa.segmentmanagement.service.SegmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for Segment management
 */
@RestController
@RequestMapping("/api/v1/segments")
@RequiredArgsConstructor
@Slf4j
public class SegmentController {

    private final SegmentService segmentService;

    /**
     * Get all segments
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SEGMENT_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<SegmentDto>> getAllSegments() {
        log.info("GET /api/v1/segments - Get all segments");
        List<SegmentDto> segments = segmentService.getAllSegments();
        return ResponseEntity.ok(segments);
    }

    /**
     * Get all active segments
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyAuthority('SEGMENT_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<List<SegmentDto>> getAllActiveSegments() {
        log.info("GET /api/v1/segments/active - Get all active segments");
        List<SegmentDto> segments = segmentService.getAllActiveSegments();
        return ResponseEntity.ok(segments);
    }

    /**
     * Get root segments
     */
    @GetMapping("/roots")
    @PreAuthorize("hasAnyAuthority('SEGMENT_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<List<SegmentDto>> getRootSegments() {
        log.info("GET /api/v1/segments/roots - Get root segments");
        List<SegmentDto> segments = segmentService.getRootSegments();
        return ResponseEntity.ok(segments);
    }

    /**
     * Get segment by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SEGMENT_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<SegmentDto> getSegmentById(@PathVariable UUID id) {
        log.info("GET /api/v1/segments/{} - Get segment by id", id);
        SegmentDto segment = segmentService.getSegmentById(id);
        return ResponseEntity.ok(segment);
    }

    /**
     * Get segment by code
     */
    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyAuthority('SEGMENT_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<SegmentDto> getSegmentByCode(@PathVariable String code) {
        log.info("GET /api/v1/segments/code/{} - Get segment by code", code);
        SegmentDto segment = segmentService.getSegmentByCode(code);
        return ResponseEntity.ok(segment);
    }

    /**
     * Get segments by type
     */
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyAuthority('SEGMENT_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<SegmentDto>> getSegmentsByType(@PathVariable String type) {
        log.info("GET /api/v1/segments/type/{} - Get segments by type", type);
        SegmentType segmentType = SegmentType.valueOf(type);
        List<SegmentDto> segments = segmentService.getSegmentsByType(segmentType);
        return ResponseEntity.ok(segments);
    }

    /**
     * Get child segments of a parent
     */
    @GetMapping("/{id}/children")
    @PreAuthorize("hasAnyAuthority('SEGMENT_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<List<SegmentDto>> getChildSegments(@PathVariable UUID id) {
        log.info("GET /api/v1/segments/{}/children - Get child segments", id);
        List<SegmentDto> segments = segmentService.getChildSegments(id);
        return ResponseEntity.ok(segments);
    }

    /**
     * Search segments by name or code
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('SEGMENT_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<List<SegmentDto>> searchSegments(@RequestParam String q) {
        log.info("GET /api/v1/segments/search?q={} - Search segments", q);
        List<SegmentDto> segments = segmentService.searchSegments(q);
        return ResponseEntity.ok(segments);
    }

    /**
     * Create a new segment
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('SEGMENT_CREATE', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<SegmentDto> createSegment(@Valid @RequestBody CreateSegmentRequest request) {
        log.info("POST /api/v1/segments - Create segment with code: {}", request.getCode());
        SegmentDto segment = segmentService.createSegment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(segment);
    }

    /**
     * Update an existing segment
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SEGMENT_UPDATE', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<SegmentDto> updateSegment(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSegmentRequest request) {
        log.info("PUT /api/v1/segments/{} - Update segment", id);
        SegmentDto segment = segmentService.updateSegment(id, request);
        return ResponseEntity.ok(segment);
    }

    /**
     * Delete a segment (soft delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SEGMENT_DELETE', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<Void> deleteSegment(@PathVariable UUID id) {
        log.info("DELETE /api/v1/segments/{} - Delete segment", id);
        segmentService.deleteSegment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activate a segment
     */
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAnyAuthority('SEGMENT_UPDATE', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<SegmentDto> activateSegment(@PathVariable UUID id) {
        log.info("POST /api/v1/segments/{}/activate - Activate segment", id);
        SegmentDto segment = segmentService.activateSegment(id);
        return ResponseEntity.ok(segment);
    }

    /**
     * Deactivate a segment
     */
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyAuthority('SEGMENT_UPDATE', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<SegmentDto> deactivateSegment(@PathVariable UUID id) {
        log.info("POST /api/v1/segments/{}/deactivate - Deactivate segment", id);
        SegmentDto segment = segmentService.deactivateSegment(id);
        return ResponseEntity.ok(segment);
    }
}
