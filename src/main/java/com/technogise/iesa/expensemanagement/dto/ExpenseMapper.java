package com.technogise.iesa.expensemanagement.dto;

import com.technogise.iesa.expensemanagement.domain.Expense;
import com.technogise.iesa.expensemanagement.domain.SegmentAllocation;
import com.technogise.iesa.expensemanagement.domain.Document;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "status", expression = "java(expense.getStatus().name())")
    @Mapping(target = "submitterId", source = "submitter.id")
    @Mapping(target = "submitterName", expression = "java(expense.getSubmitter().getFullName())")
    ExpenseDto toDto(Expense expense);

    List<ExpenseDto> toDtoList(List<Expense> expenses);

    @Mapping(target = "segmentId", source = "segment.id")
    @Mapping(target = "segmentName", source = "segment.name")
    @Mapping(target = "segmentCode", source = "segment.code")
    SegmentAllocationDto toDto(SegmentAllocation allocation);

    @Mapping(target = "uploadedById", source = "uploadedBy.id")
    @Mapping(target = "uploadedByName", expression = "java(document.getUploadedBy().getFullName())")
    DocumentDto toDto(Document document);
}
