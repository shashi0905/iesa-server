package com.technogise.iesa.reporting.dto;

import com.technogise.iesa.reporting.domain.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ReportTemplateMapper.class})
public interface ReportMapper {

    @Mapping(target = "templateId", source = "template.id")
    @Mapping(target = "templateName", source = "template.name")
    @Mapping(target = "createdByUserId", source = "createdByUser.id")
    @Mapping(target = "createdByUserName", source = "createdByUser.username")
    ReportDto toDto(Report report);

    List<ReportDto> toDtoList(List<Report> reports);
}
