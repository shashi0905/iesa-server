package com.technogise.iesa.usermanagement.dto;

import com.technogise.iesa.usermanagement.domain.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * DTO for Role entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {

    private UUID id;
    private String name;
    private RoleType roleType;
    private String description;
    private Set<PermissionDto> permissions;

}
