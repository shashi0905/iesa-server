package com.technogise.iesa.segmentmanagement.service;

import com.technogise.iesa.segmentmanagement.domain.Segment;
import com.technogise.iesa.segmentmanagement.domain.SegmentType;
import com.technogise.iesa.segmentmanagement.dto.CreateSegmentRequest;
import com.technogise.iesa.segmentmanagement.dto.SegmentDto;
import com.technogise.iesa.segmentmanagement.dto.SegmentMapper;
import com.technogise.iesa.segmentmanagement.dto.UpdateSegmentRequest;
import com.technogise.iesa.segmentmanagement.repository.SegmentRepository;
import com.technogise.iesa.shared.exception.DuplicateResourceException;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for Segment business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SegmentService {

    private final SegmentRepository segmentRepository;
    private final SegmentMapper segmentMapper;

    /**
     * Get all segments
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "segments", key = "'all'")
    public List<SegmentDto> getAllSegments() {
        log.debug("Fetching all segments");
        return segmentRepository.findAllNotDeleted()
                .stream()
                .map(segmentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all active segments
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "segments", key = "'active'")
    public List<SegmentDto> getAllActiveSegments() {
        log.debug("Fetching all active segments");
        return segmentRepository.findAllActive()
                .stream()
                .map(segmentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get segment by ID
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "segments", key = "#id")
    public SegmentDto getSegmentById(UUID id) {
        log.debug("Fetching segment by id: {}", id);
        Segment segment = segmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Segment not found with id: " + id));
        return segmentMapper.toDto(segment);
    }

    /**
     * Get segment by code
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "segments", key = "'code:' + #code")
    public SegmentDto getSegmentByCode(String code) {
        log.debug("Fetching segment by code: {}", code);
        Segment segment = segmentRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Segment not found with code: " + code));
        return segmentMapper.toDto(segment);
    }

    /**
     * Get segments by type
     */
    @Transactional(readOnly = true)
    public List<SegmentDto> getSegmentsByType(SegmentType type) {
        log.debug("Fetching segments by type: {}", type);
        return segmentRepository.findBySegmentType(type)
                .stream()
                .map(segmentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get root segments (no parent)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "segments", key = "'roots'")
    public List<SegmentDto> getRootSegments() {
        log.debug("Fetching root segments");
        return segmentRepository.findRootSegments()
                .stream()
                .map(segmentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get child segments of a parent
     */
    @Transactional(readOnly = true)
    public List<SegmentDto> getChildSegments(UUID parentId) {
        log.debug("Fetching child segments for parent: {}", parentId);
        return segmentRepository.findByParentSegmentId(parentId)
                .stream()
                .map(segmentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Search segments by name or code
     */
    @Transactional(readOnly = true)
    public List<SegmentDto> searchSegments(String searchTerm) {
        log.debug("Searching segments with term: {}", searchTerm);
        return segmentRepository.searchSegments(searchTerm)
                .stream()
                .map(segmentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Create a new segment
     */
    @CacheEvict(value = "segments", allEntries = true)
    public SegmentDto createSegment(CreateSegmentRequest request) {
        log.info("Creating new segment with code: {}", request.getCode());

        // Check if code already exists
        if (segmentRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Segment already exists with code: " + request.getCode());
        }

        // Parse segment type
        SegmentType segmentType;
        try {
            segmentType = SegmentType.valueOf(request.getSegmentType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid segment type: " + request.getSegmentType());
        }

        // Create segment entity
        Segment segment = segmentMapper.toEntity(request);
        segment.setSegmentType(segmentType);

        // Set parent segment if provided
        if (request.getParentSegmentId() != null) {
            Segment parentSegment = segmentRepository.findById(request.getParentSegmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent segment not found with id: " + request.getParentSegmentId()));
            segment.setParentSegment(parentSegment);
        }

        // Save segment
        segment = segmentRepository.save(segment);
        log.info("Segment created successfully with id: {}", segment.getId());

        return segmentMapper.toDto(segment);
    }

    /**
     * Update an existing segment
     */
    @CacheEvict(value = "segments", allEntries = true)
    public SegmentDto updateSegment(UUID id, UpdateSegmentRequest request) {
        log.info("Updating segment with id: {}", id);

        // Find existing segment
        Segment segment = segmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Segment not found with id: " + id));

        // Update segment type if provided
        if (request.getSegmentType() != null) {
            try {
                SegmentType segmentType = SegmentType.valueOf(request.getSegmentType());
                segment.setSegmentType(segmentType);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid segment type: " + request.getSegmentType());
            }
        }

        // Update parent segment if provided
        if (request.getParentSegmentId() != null) {
            // Prevent setting self as parent
            if (request.getParentSegmentId().equals(id)) {
                throw new IllegalArgumentException("Segment cannot be its own parent");
            }
            Segment parentSegment = segmentRepository.findById(request.getParentSegmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent segment not found with id: " + request.getParentSegmentId()));
            segment.setParentSegment(parentSegment);
        }

        // Update other fields
        segmentMapper.updateEntityFromDto(request, segment);

        // Save updated segment
        segment = segmentRepository.save(segment);
        log.info("Segment updated successfully with id: {}", segment.getId());

        return segmentMapper.toDto(segment);
    }

    /**
     * Delete a segment (soft delete)
     */
    @CacheEvict(value = "segments", allEntries = true)
    public void deleteSegment(UUID id) {
        log.info("Deleting segment with id: {}", id);

        Segment segment = segmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Segment not found with id: " + id));

        // Check if segment has children
        List<Segment> children = segmentRepository.findByParentSegmentId(id);
        if (!children.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete segment with children. Please delete or reassign child segments first.");
        }

        // Soft delete
        segment.setDeletedAt(java.time.Instant.now());
        segmentRepository.save(segment);

        log.info("Segment deleted successfully with id: {}", id);
    }

    /**
     * Activate a segment
     */
    @CacheEvict(value = "segments", allEntries = true)
    public SegmentDto activateSegment(UUID id) {
        log.info("Activating segment with id: {}", id);

        Segment segment = segmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Segment not found with id: " + id));

        segment.setIsActive(true);
        segment = segmentRepository.save(segment);

        log.info("Segment activated successfully with id: {}", id);
        return segmentMapper.toDto(segment);
    }

    /**
     * Deactivate a segment
     */
    @CacheEvict(value = "segments", allEntries = true)
    public SegmentDto deactivateSegment(UUID id) {
        log.info("Deactivating segment with id: {}", id);

        Segment segment = segmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Segment not found with id: " + id));

        segment.setIsActive(false);
        segment = segmentRepository.save(segment);

        log.info("Segment deactivated successfully with id: {}", id);
        return segmentMapper.toDto(segment);
    }
}
