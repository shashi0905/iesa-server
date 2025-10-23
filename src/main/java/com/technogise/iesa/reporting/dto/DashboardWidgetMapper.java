package com.technogise.iesa.reporting.dto;

import com.technogise.iesa.reporting.domain.DashboardWidget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DashboardWidgetMapper {

    @Mapping(target = "dashboardId", source = "dashboard.id")
    @Mapping(target = "reportId", source = "report.id")
    @Mapping(target = "reportName", source = "report.name")
    DashboardWidgetDto toDto(DashboardWidget dashboardWidget);

    List<DashboardWidgetDto> toDtoList(List<DashboardWidget> dashboardWidgets);
}
