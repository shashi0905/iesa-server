package com.technogise.iesa.reporting.dto;

import com.technogise.iesa.reporting.domain.AnalyticsSnapshot;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnalyticsSnapshotMapper {

    AnalyticsSnapshotDto toDto(AnalyticsSnapshot analyticsSnapshot);

    List<AnalyticsSnapshotDto> toDtoList(List<AnalyticsSnapshot> analyticsSnapshots);
}
