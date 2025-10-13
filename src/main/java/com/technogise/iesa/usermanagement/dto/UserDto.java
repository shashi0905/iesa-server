package com.technogise.iesa.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for User entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UUID departmentId;
    private String departmentName;
    private Set<RoleDto> roles;
    private Boolean isActive;
    private Boolean accountLocked;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Get full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

}
