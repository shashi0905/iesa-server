package com.company.iesa.usermanagement.dto;

import com.company.iesa.usermanagement.domain.Department;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Department entity and DTOs
 */
@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    @Mapping(source = "parentDepartment.id", target = "parentDepartmentId")
    @Mapping(source = "parentDepartment.name", target = "parentDepartmentName")
    @Mapping(source = "manager.id", target = "managerId")
    @Mapping(source = "manager.username", target = "managerName")
    DepartmentDto toDto(Department department);

    List<DepartmentDto> toDtoList(List<Department> departments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentDepartment", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Department toEntity(CreateDepartmentRequest request);

}
