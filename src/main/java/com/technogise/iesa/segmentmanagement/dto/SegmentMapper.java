package com.technogise.iesa.segmentmanagement.dto;

import com.technogise.iesa.segmentmanagement.domain.Segment;
import org.mapstruct.*;

/**
 * MapStruct mapper for Segment entity
 */
@Mapper(componentModel = "spring")
public interface SegmentMapper {

    @Mapping(target = "segmentType", expression = "java(segment.getSegmentType().name())")
    @Mapping(target = "parentSegmentId", source = "parentSegment.id")
    @Mapping(target = "parentSegmentName", source = "parentSegment.name")
    @Mapping(target = "fullPath", expression = "java(segment.getFullPath())")
    @Mapping(target = "isRoot", expression = "java(segment.isRoot())")
    @Mapping(target = "isLeaf", expression = "java(segment.isLeaf())")
    SegmentDto toDto(Segment segment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "parentSegment", ignore = true)
    @Mapping(target = "childSegments", ignore = true)
    @Mapping(target = "segmentType", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Segment toEntity(CreateSegmentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "parentSegment", ignore = true)
    @Mapping(target = "childSegments", ignore = true)
    @Mapping(target = "segmentType", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateSegmentRequest request, @MappingTarget Segment segment);
}
