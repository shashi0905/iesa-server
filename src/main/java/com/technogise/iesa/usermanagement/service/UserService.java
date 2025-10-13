package com.technogise.iesa.usermanagement.service;

import com.technogise.iesa.shared.exception.DuplicateResourceException;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import com.technogise.iesa.usermanagement.domain.Department;
import com.technogise.iesa.usermanagement.domain.Role;
import com.technogise.iesa.usermanagement.domain.User;
import com.technogise.iesa.usermanagement.dto.*;
import com.technogise.iesa.usermanagement.repository.DepartmentRepository;
import com.technogise.iesa.usermanagement.repository.RoleRepository;
import com.technogise.iesa.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service for managing users
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get all users
     */
    @Cacheable(value = "users", key = "'all'")
    public List<UserDto> getAllUsers() {
        log.debug("Fetching all users");
        List<User> users = userRepository.findAll();
        return userMapper.toDtoList(users);
    }

    /**
     * Get user by ID
     */
    @Cacheable(value = "users", key = "#id")
    public UserDto getUserById(UUID id) {
        log.debug("Fetching user with id: {}", id);
        User user = findUserByIdOrThrow(id);
        return userMapper.toDto(user);
    }

    /**
     * Get user by username
     */
    @Cacheable(value = "users", key = "'username:' + #username")
    public UserDto getUserByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return userMapper.toDto(user);
    }

    /**
     * Get users by department
     */
    public List<UserDto> getUsersByDepartment(UUID departmentId) {
        log.debug("Fetching users in department: {}", departmentId);
        List<User> users = userRepository.findByDepartmentId(departmentId);
        return userMapper.toDtoList(users);
    }

    /**
     * Get active users
     */
    public List<UserDto> getActiveUsers() {
        log.debug("Fetching active users");
        List<User> users = userRepository.findAllActive();
        return userMapper.toDtoList(users);
    }

    /**
     * Create a new user
     */
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserDto createUser(CreateUserRequest request) {
        log.debug("Creating new user with username: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Map to entity
        User user = userMapper.toEntity(request);

        // Hash password
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Set department
        Department department = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
        user.setDepartment(department);

        // Set roles
        Set<Role> roles = new HashSet<>();
        for (UUID roleId : request.getRoleIds()) {
            Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
            roles.add(role);
        }
        user.setRoles(roles);

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    /**
     * Update an existing user
     */
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserDto updateUser(UUID id, UpdateUserRequest request) {
        log.debug("Updating user with id: {}", id);

        User user = findUserByIdOrThrow(id);

        // Check email uniqueness if changed
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("User", "email", request.getEmail());
            }
        }

        // Update basic fields
        userMapper.updateEntityFromDto(request, user);

        // Update department if provided
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            user.setDepartment(department);
        }

        // Update roles if provided
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (UUID roleId : request.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", updatedUser.getId());

        return userMapper.toDto(updatedUser);
    }

    /**
     * Delete user (soft delete)
     */
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(UUID id) {
        log.debug("Deleting user with id: {}", id);

        User user = findUserByIdOrThrow(id);
        user.softDelete();
        userRepository.save(user);

        log.info("User soft deleted successfully with id: {}", id);
    }

    /**
     * Lock user account
     */
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void lockUserAccount(UUID id) {
        log.debug("Locking user account with id: {}", id);

        User user = findUserByIdOrThrow(id);
        user.lockAccount();
        userRepository.save(user);

        log.info("User account locked successfully with id: {}", id);
    }

    /**
     * Unlock user account
     */
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void unlockUserAccount(UUID id) {
        log.debug("Unlocking user account with id: {}", id);

        User user = findUserByIdOrThrow(id);
        user.unlockAccount();
        userRepository.save(user);

        log.info("User account unlocked successfully with id: {}", id);
    }

    /**
     * Change user password
     */
    @Transactional
    public void changePassword(UUID id, String newPassword) {
        log.debug("Changing password for user with id: {}", id);

        User user = findUserByIdOrThrow(id);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed successfully for user with id: {}", id);
    }

    /**
     * Helper method to find user by ID or throw exception
     */
    private User findUserByIdOrThrow(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

}
