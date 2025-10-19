package com.technogise.iesa.usermanagement.service;

import com.technogise.iesa.usermanagement.dto.RoleDto;
import com.technogise.iesa.usermanagement.dto.RoleMapper;
import com.technogise.iesa.usermanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for Role management
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    /**
     * Get all roles (excluding soft-deleted)
     */
    public List<RoleDto> getAllRoles() {
        return roleMapper.toDtoList(roleRepository.findAll()
            .stream()
            .filter(role -> role.getDeletedAt() == null)
            .toList());
    }
}
