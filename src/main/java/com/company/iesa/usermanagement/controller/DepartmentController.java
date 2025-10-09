package com.company.iesa.usermanagement.controller;

import com.company.iesa.usermanagement.dto.CreateDepartmentRequest;
import com.company.iesa.usermanagement.dto.DepartmentDto;
import com.company.iesa.usermanagement.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for department management
 */
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * Get all departments
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('DEPARTMENT_READ', 'ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        log.info("GET /api/v1/departments - Get all departments");
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    /**
     * Get department by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('DEPARTMENT_READ', 'ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable UUID id) {
        log.info("GET /api/v1/departments/{} - Get department by ID", id);
        DepartmentDto department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    /**
     * Get root departments
     */
    @GetMapping("/root")
    @PreAuthorize("hasAnyAuthority('DEPARTMENT_READ', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<List<DepartmentDto>> getRootDepartments() {
        log.info("GET /api/v1/departments/root - Get root departments");
        List<DepartmentDto> departments = departmentService.getRootDepartments();
        return ResponseEntity.ok(departments);
    }

    /**
     * Get child departments
     */
    @GetMapping("/{id}/children")
    @PreAuthorize("hasAnyAuthority('DEPARTMENT_READ', 'ROLE_MANAGER', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<List<DepartmentDto>> getChildDepartments(@PathVariable UUID id) {
        log.info("GET /api/v1/departments/{}/children - Get child departments", id);
        List<DepartmentDto> departments = departmentService.getChildDepartments(id);
        return ResponseEntity.ok(departments);
    }

    /**
     * Get active departments
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyAuthority('DEPARTMENT_READ', 'ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<List<DepartmentDto>> getActiveDepartments() {
        log.info("GET /api/v1/departments/active - Get active departments");
        List<DepartmentDto> departments = departmentService.getActiveDepartments();
        return ResponseEntity.ok(departments);
    }

    /**
     * Create a new department
     */
    @PostMapping
    @PreAuthorize("hasAuthority('DEPARTMENT_CREATE') or hasRole('ROLE_FINANCE_ADMIN')")
    public ResponseEntity<DepartmentDto> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        log.info("POST /api/v1/departments - Create new department: {}", request.getName());
        DepartmentDto createdDepartment = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
    }

    /**
     * Update an existing department
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DEPARTMENT_UPDATE') or hasRole('ROLE_FINANCE_ADMIN')")
    public ResponseEntity<DepartmentDto> updateDepartment(
            @PathVariable UUID id,
            @Valid @RequestBody CreateDepartmentRequest request) {
        log.info("PUT /api/v1/departments/{} - Update department", id);
        DepartmentDto updatedDepartment = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(updatedDepartment);
    }

    /**
     * Delete department (soft delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DEPARTMENT_DELETE') or hasRole('ROLE_FINANCE_ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable UUID id) {
        log.info("DELETE /api/v1/departments/{} - Delete department", id);
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activate department
     */
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('DEPARTMENT_UPDATE') or hasRole('ROLE_FINANCE_ADMIN')")
    public ResponseEntity<Void> activateDepartment(@PathVariable UUID id) {
        log.info("POST /api/v1/departments/{}/activate - Activate department", id);
        departmentService.setDepartmentActive(id, true);
        return ResponseEntity.ok().build();
    }

    /**
     * Deactivate department
     */
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('DEPARTMENT_UPDATE') or hasRole('ROLE_FINANCE_ADMIN')")
    public ResponseEntity<Void> deactivateDepartment(@PathVariable UUID id) {
        log.info("POST /api/v1/departments/{}/deactivate - Deactivate department", id);
        departmentService.setDepartmentActive(id, false);
        return ResponseEntity.ok().build();
    }

}
