package com.technogise.iesa.usermanagement.dto;

import com.technogise.iesa.usermanagement.domain.Role;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for Role entity and DTOs
 */
@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {

    RoleDto toDto(Role role);

    List<RoleDto> toDtoList(List<Role> roles);

    Set<RoleDto> toDtoSet(Set<Role> roles);

}
