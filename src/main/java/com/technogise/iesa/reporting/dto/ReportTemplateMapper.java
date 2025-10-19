package com.technogise.iesa.reporting.dto;

import com.technogise.iesa.reporting.domain.ReportTemplate;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportTemplateMapper {

    ReportTemplateDto toDto(ReportTemplate reportTemplate);

    List<ReportTemplateDto> toDtoList(List<ReportTemplate> reportTemplates);
}
