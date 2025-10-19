package com.technogise.iesa.reporting.dto;

import com.technogise.iesa.reporting.domain.Dashboard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DashboardWidgetMapper.class})
public interface DashboardMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", source = "owner.username")
    DashboardDto toDto(Dashboard dashboard);

    List<DashboardDto> toDtoList(List<Dashboard> dashboards);
}
