package com.technogise.iesa.usermanagement.dto;

import com.technogise.iesa.usermanagement.domain.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for Permission entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDto {

    private UUID id;
    private PermissionType permissionType;
    private String description;
    private String resource;
    private String action;

}
