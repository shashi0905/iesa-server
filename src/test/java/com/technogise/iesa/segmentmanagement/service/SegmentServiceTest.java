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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SegmentServiceTest {

    @Mock
    private SegmentRepository segmentRepository;

    @Mock
    private SegmentMapper segmentMapper;

    @InjectMocks
    private SegmentService segmentService;

    private Segment segment;
    private SegmentDto segmentDto;
    private CreateSegmentRequest createRequest;
    private UpdateSegmentRequest updateRequest;
    private UUID segmentId;

    @BeforeEach
    void setUp() {
        segmentId = UUID.randomUUID();

        segment = Segment.builder()
                .id(segmentId)
                .name("Engineering")
                .code("ENG-001")
                .description("Engineering Department")
                .segmentType(SegmentType.COST_CENTER)
                .isActive(true)
                .displayOrder(1)
                .build();

        segmentDto = SegmentDto.builder()
                .id(segmentId)
                .name("Engineering")
                .code("ENG-001")
                .description("Engineering Department")
                .segmentType(SegmentType.COST_CENTER.name())
                .isActive(true)
                .displayOrder(1)
                .build();

        createRequest = CreateSegmentRequest.builder()
                .name("Engineering")
                .code("ENG-001")
                .description("Engineering Department")
                .segmentType(SegmentType.COST_CENTER.name())
                .displayOrder(1)
                .build();

        updateRequest = UpdateSegmentRequest.builder()
                .name("Engineering Updated")
                .description("Updated Engineering Department")
                .build();
    }

    @Test
    void getAllSegments_ShouldReturnAllSegments() {
        // Arrange
        List<Segment> segments = Arrays.asList(segment);

        when(segmentRepository.findAllNotDeleted()).thenReturn(segments);
        when(segmentMapper.toDto(segment)).thenReturn(segmentDto);

        // Act
        List<SegmentDto> result = segmentService.getAllSegments();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Engineering");
        verify(segmentRepository, times(1)).findAllNotDeleted();
        verify(segmentMapper, times(1)).toDto(segment);
    }

    @Test
    void getAllActiveSegments_ShouldReturnOnlyActiveSegments() {
        // Arrange
        List<Segment> segments = Arrays.asList(segment);

        when(segmentRepository.findAllActive()).thenReturn(segments);
        when(segmentMapper.toDto(segment)).thenReturn(segmentDto);

        // Act
        List<SegmentDto> result = segmentService.getAllActiveSegments();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isTrue();
        verify(segmentRepository, times(1)).findAllActive();
        verify(segmentMapper, times(1)).toDto(segment);
    }

    @Test
    void getSegmentById_WhenExists_ShouldReturnSegment() {
        // Arrange
        when(segmentRepository.findById(segmentId)).thenReturn(Optional.of(segment));
        when(segmentMapper.toDto(segment)).thenReturn(segmentDto);

        // Act
        SegmentDto result = segmentService.getSegmentById(segmentId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(segmentId);
        assertThat(result.getName()).isEqualTo("Engineering");
        verify(segmentRepository, times(1)).findById(segmentId);
    }

    @Test
    void getSegmentById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(segmentRepository.findById(segmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> segmentService.getSegmentById(segmentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Segment not found with id");

        verify(segmentRepository, times(1)).findById(segmentId);
        verify(segmentMapper, never()).toDto(any());
    }

    @Test
    void getSegmentByCode_WhenExists_ShouldReturnSegment() {
        // Arrange
        when(segmentRepository.findByCode("ENG-001")).thenReturn(Optional.of(segment));
        when(segmentMapper.toDto(segment)).thenReturn(segmentDto);

        // Act
        SegmentDto result = segmentService.getSegmentByCode("ENG-001");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("ENG-001");
        verify(segmentRepository, times(1)).findByCode("ENG-001");
    }

    @Test
    void createSegment_WithValidData_ShouldCreateSuccessfully() {
        // Arrange
        when(segmentRepository.existsByCode("ENG-001")).thenReturn(false);
        when(segmentRepository.save(any(Segment.class))).thenReturn(segment);
        when(segmentMapper.toDto(segment)).thenReturn(segmentDto);

        // Act
        SegmentDto result = segmentService.createSegment(createRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Engineering");
        assertThat(result.getCode()).isEqualTo("ENG-001");
        verify(segmentRepository, times(1)).existsByCode("ENG-001");
        verify(segmentRepository, times(1)).save(any(Segment.class));
    }

    @Test
    void createSegment_WithDuplicateCode_ShouldThrowException() {
        // Arrange
        when(segmentRepository.existsByCode("ENG-001")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> segmentService.createSegment(createRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        verify(segmentRepository, times(1)).existsByCode("ENG-001");
        verify(segmentRepository, never()).save(any());
    }

    @Test
    void updateSegment_WithValidData_ShouldUpdateSuccessfully() {
        // Arrange
        when(segmentRepository.findById(segmentId)).thenReturn(Optional.of(segment));
        when(segmentRepository.save(any(Segment.class))).thenReturn(segment);
        when(segmentMapper.toDto(segment)).thenReturn(segmentDto);

        // Act
        SegmentDto result = segmentService.updateSegment(segmentId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        verify(segmentRepository, times(1)).findById(segmentId);
        verify(segmentRepository, times(1)).save(any(Segment.class));
    }

    @Test
    void deleteSegment_WhenExists_ShouldSoftDelete() {
        // Arrange
        when(segmentRepository.findById(segmentId)).thenReturn(Optional.of(segment));

        // Act
        segmentService.deleteSegment(segmentId);

        // Assert
        assertThat(segment.getDeletedAt()).isNotNull();
        verify(segmentRepository, times(1)).findById(segmentId);
        verify(segmentRepository, times(1)).save(segment);
    }

    @Test
    void activateSegment_WhenExists_ShouldActivate() {
        // Arrange
        segment.setIsActive(false);
        when(segmentRepository.findById(segmentId)).thenReturn(Optional.of(segment));
        when(segmentRepository.save(segment)).thenReturn(segment);
        when(segmentMapper.toDto(segment)).thenReturn(segmentDto);

        // Act
        SegmentDto result = segmentService.activateSegment(segmentId);

        // Assert
        assertThat(segment.getIsActive()).isTrue();
        verify(segmentRepository, times(1)).findById(segmentId);
        verify(segmentRepository, times(1)).save(segment);
    }

    @Test
    void deactivateSegment_WhenExists_ShouldDeactivate() {
        // Arrange
        when(segmentRepository.findById(segmentId)).thenReturn(Optional.of(segment));
        when(segmentRepository.save(segment)).thenReturn(segment);
        when(segmentMapper.toDto(segment)).thenReturn(segmentDto);

        // Act
        SegmentDto result = segmentService.deactivateSegment(segmentId);

        // Assert
        assertThat(segment.getIsActive()).isFalse();
        verify(segmentRepository, times(1)).findById(segmentId);
        verify(segmentRepository, times(1)).save(segment);
    }

    @Test
    void getSegmentsByType_ShouldReturnSegmentsOfType() {
        // Arrange
        List<Segment> segments = Arrays.asList(segment);

        when(segmentRepository.findBySegmentType(SegmentType.COST_CENTER)).thenReturn(segments);
        when(segmentMapper.toDto(segment)).thenReturn(segmentDto);

        // Act
        List<SegmentDto> result = segmentService.getSegmentsByType(SegmentType.COST_CENTER);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSegmentType()).isEqualTo(SegmentType.COST_CENTER.name());
        verify(segmentRepository, times(1)).findBySegmentType(SegmentType.COST_CENTER);
        verify(segmentMapper, times(1)).toDto(segment);
    }

    @Test
    void searchSegments_ShouldReturnMatchingSegments() {
        // Arrange
        List<Segment> segments = Arrays.asList(segment);

        when(segmentRepository.searchSegments("Eng")).thenReturn(segments);
        when(segmentMapper.toDto(segment)).thenReturn(segmentDto);

        // Act
        List<SegmentDto> result = segmentService.searchSegments("Eng");

        // Assert
        assertThat(result).hasSize(1);
        verify(segmentRepository, times(1)).searchSegments("Eng");
        verify(segmentMapper, times(1)).toDto(segment);
    }
}
