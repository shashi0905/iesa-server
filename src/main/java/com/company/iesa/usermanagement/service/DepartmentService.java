package com.company.iesa.usermanagement.service;

import com.company.iesa.shared.exception.DuplicateResourceException;
import com.company.iesa.shared.exception.ResourceNotFoundException;
import com.company.iesa.usermanagement.domain.Department;
import com.company.iesa.usermanagement.domain.User;
import com.company.iesa.usermanagement.dto.CreateDepartmentRequest;
import com.company.iesa.usermanagement.dto.DepartmentDto;
import com.company.iesa.usermanagement.dto.DepartmentMapper;
import com.company.iesa.usermanagement.repository.DepartmentRepository;
import com.company.iesa.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing departments
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final DepartmentMapper departmentMapper;

    /**
     * Get all departments
     */
    @Cacheable(value = "departments", key = "'all'")
    public List<DepartmentDto> getAllDepartments() {
        log.debug("Fetching all departments");
        List<Department> departments = departmentRepository.findAll();
        return departmentMapper.toDtoList(departments);
    }

    /**
     * Get department by ID
     */
    @Cacheable(value = "departments", key = "#id")
    public DepartmentDto getDepartmentById(UUID id) {
        log.debug("Fetching department with id: {}", id);
        Department department = findDepartmentByIdOrThrow(id);
        return departmentMapper.toDto(department);
    }

    /**
     * Get root departments (no parent)
     */
    public List<DepartmentDto> getRootDepartments() {
        log.debug("Fetching root departments");
        List<Department> departments = departmentRepository.findRootDepartments();
        return departmentMapper.toDtoList(departments);
    }

    /**
     * Get child departments
     */
    public List<DepartmentDto> getChildDepartments(UUID parentId) {
        log.debug("Fetching child departments for parent: {}", parentId);
        List<Department> departments = departmentRepository.findByParentDepartmentId(parentId);
        return departmentMapper.toDtoList(departments);
    }

    /**
     * Get active departments
     */
    public List<DepartmentDto> getActiveDepartments() {
        log.debug("Fetching active departments");
        List<Department> departments = departmentRepository.findAllActive();
        return departmentMapper.toDtoList(departments);
    }

    /**
     * Create a new department
     */
    @Transactional
    @CacheEvict(value = "departments", allEntries = true)
    public DepartmentDto createDepartment(CreateDepartmentRequest request) {
        log.debug("Creating new department with code: {}", request.getCode());

        // Check if code already exists
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Department", "code", request.getCode());
        }

        // Map to entity
        Department department = departmentMapper.toEntity(request);

        // Set parent department if provided
        if (request.getParentDepartmentId() != null) {
            Department parentDepartment = departmentRepository.findById(request.getParentDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getParentDepartmentId()));
            department.setParentDepartment(parentDepartment);
        }

        // Set manager if provided
        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getManagerId()));
            department.setManager(manager);
        }

        // Save department
        Department savedDepartment = departmentRepository.save(department);
        log.info("Department created successfully with id: {}", savedDepartment.getId());

        return departmentMapper.toDto(savedDepartment);
    }

    /**
     * Update department
     */
    @Transactional
    @CacheEvict(value = "departments", allEntries = true)
    public DepartmentDto updateDepartment(UUID id, CreateDepartmentRequest request) {
        log.debug("Updating department with id: {}", id);

        Department department = findDepartmentByIdOrThrow(id);

        // Check code uniqueness if changed
        if (!request.getCode().equals(department.getCode())) {
            if (departmentRepository.existsByCode(request.getCode())) {
                throw new DuplicateResourceException("Department", "code", request.getCode());
            }
        }

        // Update fields
        department.setName(request.getName());
        department.setCode(request.getCode());
        department.setDescription(request.getDescription());
        department.setCostCenter(request.getCostCenter());

        // Update parent department if provided
        if (request.getParentDepartmentId() != null) {
            Department parentDepartment = departmentRepository.findById(request.getParentDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getParentDepartmentId()));
            department.setParentDepartment(parentDepartment);
        }

        // Update manager if provided
        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getManagerId()));
            department.setManager(manager);
        }

        Department updatedDepartment = departmentRepository.save(department);
        log.info("Department updated successfully with id: {}", updatedDepartment.getId());

        return departmentMapper.toDto(updatedDepartment);
    }

    /**
     * Delete department (soft delete)
     */
    @Transactional
    @CacheEvict(value = "departments", allEntries = true)
    public void deleteDepartment(UUID id) {
        log.debug("Deleting department with id: {}", id);

        Department department = findDepartmentByIdOrThrow(id);
        department.softDelete();
        departmentRepository.save(department);

        log.info("Department soft deleted successfully with id: {}", id);
    }

    /**
     * Activate/Deactivate department
     */
    @Transactional
    @CacheEvict(value = "departments", key = "#id")
    public void setDepartmentActive(UUID id, boolean isActive) {
        log.debug("Setting department {} active status to: {}", id, isActive);

        Department department = findDepartmentByIdOrThrow(id);
        department.setIsActive(isActive);
        departmentRepository.save(department);

        log.info("Department active status updated successfully for id: {}", id);
    }

    /**
     * Helper method to find department by ID or throw exception
     */
    private Department findDepartmentByIdOrThrow(UUID id) {
        return departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }

}
