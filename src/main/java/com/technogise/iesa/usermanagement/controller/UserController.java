package com.technogise.iesa.usermanagement.controller;

import com.technogise.iesa.usermanagement.dto.CreateUserRequest;
import com.technogise.iesa.usermanagement.dto.UpdateUserRequest;
import com.technogise.iesa.usermanagement.dto.UserDto;
import com.technogise.iesa.usermanagement.service.UserService;
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
 * REST controller for user management
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Get all users
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_AUDITOR')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("GET /api/v1/users - Get all users");
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER_READ', 'ROLE_FINANCE_ADMIN', 'ROLE_AUDITOR') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        log.info("GET /api/v1/users/{} - Get user by ID", id);
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Get users by department
     */
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyAuthority('USER_READ', 'ROLE_MANAGER', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByDepartment(@PathVariable UUID departmentId) {
        log.info("GET /api/v1/users/department/{} - Get users by department", departmentId);
        List<UserDto> users = userService.getUsersByDepartment(departmentId);
        return ResponseEntity.ok(users);
    }

    /**
     * Get active users
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyAuthority('USER_READ', 'ROLE_FINANCE_ADMIN')")
    public ResponseEntity<List<UserDto>> getActiveUsers() {
        log.info("GET /api/v1/users/active - Get active users");
        List<UserDto> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Create a new user
     */
    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE') or hasRole('ROLE_FINANCE_ADMIN')")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("POST /api/v1/users - Create new user: {}", request.getUsername());
        UserDto createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Update an existing user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE') or hasRole('ROLE_FINANCE_ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("PUT /api/v1/users/{} - Update user", id);
        UserDto updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete user (soft delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE') or hasRole('ROLE_FINANCE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("DELETE /api/v1/users/{} - Delete user", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lock user account
     */
    @PostMapping("/{id}/lock")
    @PreAuthorize("hasAuthority('USER_UPDATE') or hasRole('ROLE_FINANCE_ADMIN')")
    public ResponseEntity<Void> lockUserAccount(@PathVariable UUID id) {
        log.info("POST /api/v1/users/{}/lock - Lock user account", id);
        userService.lockUserAccount(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Unlock user account
     */
    @PostMapping("/{id}/unlock")
    @PreAuthorize("hasAuthority('USER_UPDATE') or hasRole('ROLE_FINANCE_ADMIN')")
    public ResponseEntity<Void> unlockUserAccount(@PathVariable UUID id) {
        log.info("POST /api/v1/users/{}/unlock - Unlock user account", id);
        userService.unlockUserAccount(id);
        return ResponseEntity.ok().build();
    }

}
