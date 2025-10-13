package com.technogise.iesa.usermanagement.security;

import com.technogise.iesa.shared.exception.DuplicateResourceException;
import com.technogise.iesa.usermanagement.domain.Role;
import com.technogise.iesa.usermanagement.domain.RoleType;
import com.technogise.iesa.usermanagement.domain.User;
import com.technogise.iesa.usermanagement.dto.AuthenticationResponse;
import com.technogise.iesa.usermanagement.dto.LoginRequest;
import com.technogise.iesa.usermanagement.dto.RegisterRequest;
import com.technogise.iesa.usermanagement.dto.UserDto;
import com.technogise.iesa.usermanagement.dto.UserMapper;
import com.technogise.iesa.usermanagement.repository.RoleRepository;
import com.technogise.iesa.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Service for authentication operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Value("${iesa.jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    /**
     * Register a new user with default EMPLOYEE role
     */
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        log.info("Attempting to register new user: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            log.error("Username already exists: {}", request.getUsername());
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Email already exists: {}", request.getEmail());
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        // Get default EMPLOYEE role
        Role employeeRole = roleRepository.findByRoleType(RoleType.EMPLOYEE)
                .orElseThrow(() -> new RuntimeException("Default EMPLOYEE role not found. Please run database migrations."));

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .isActive(true)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .roles(Set.of(employeeRole))
                .build();

        // Save user
        user = userRepository.save(user);
        log.info("User {} registered successfully", user.getUsername());

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Map user to DTO
        UserDto userDto = userMapper.toDto(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration)
                .user(userDto)
                .build();
    }

    /**
     * Authenticate user and generate tokens
     */
    @Transactional
    public AuthenticationResponse login(LoginRequest request) {
        log.info("Attempting to authenticate user: {}", request.getUsernameOrEmail());

        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

            // Load user details
            User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Reset failed login attempts on successful login
            if (user.getFailedLoginAttempts() > 0) {
                user.resetFailedLoginAttempts();
                userRepository.save(user);
            }

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // Map user to DTO
            UserDto userDto = userMapper.toDto(user);

            log.info("User {} authenticated successfully", user.getUsername());

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(accessTokenExpiration)
                    .user(userDto)
                    .build();

        } catch (Exception ex) {
            log.error("Authentication failed for user: {}", request.getUsernameOrEmail());

            // Increment failed login attempts
            userRepository.findByUsernameOrEmail(request.getUsernameOrEmail())
                    .ifPresent(user -> {
                        user.incrementFailedLoginAttempts();
                        userRepository.save(user);

                        if (user.getAccountLocked()) {
                            log.warn("Account locked for user: {} due to multiple failed login attempts", user.getUsername());
                        }
                    });

            throw new BadCredentialsException("Invalid username or password");
        }
    }

    /**
     * Refresh access token using refresh token
     */
    @Transactional(readOnly = true)
    public AuthenticationResponse refreshToken(String refreshToken) {
        log.info("Attempting to refresh access token");

        // Extract username from refresh token
        String username = jwtService.extractUsername(refreshToken);

        // Load user details
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Validate refresh token
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(user);

        // Map user to DTO
        UserDto userDto = userMapper.toDto(user);

        log.info("Access token refreshed successfully for user: {}", username);

        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Return same refresh token
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration)
                .user(userDto)
                .build();
    }

}
