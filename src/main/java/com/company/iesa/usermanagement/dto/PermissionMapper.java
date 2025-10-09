package com.company.iesa.usermanagement.dto;

import com.company.iesa.usermanagement.domain.Permission;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for Permission entity and DTOs
 */
@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionDto toDto(Permission permission);

    List<PermissionDto> toDtoList(List<Permission> permissions);

    Set<PermissionDto> toDtoSet(Set<Permission> permissions);

}
