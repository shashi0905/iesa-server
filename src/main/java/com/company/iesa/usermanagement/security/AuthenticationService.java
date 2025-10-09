package com.company.iesa.usermanagement.security;

import com.company.iesa.usermanagement.domain.User;
import com.company.iesa.usermanagement.dto.AuthenticationResponse;
import com.company.iesa.usermanagement.dto.LoginRequest;
import com.company.iesa.usermanagement.dto.UserDto;
import com.company.iesa.usermanagement.dto.UserMapper;
import com.company.iesa.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserMapper userMapper;

    @Value("${iesa.jwt.access-token-expiration}")
    private Long accessTokenExpiration;

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
