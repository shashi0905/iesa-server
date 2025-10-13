package com.technogise.iesa.usermanagement.controller;

import com.technogise.iesa.usermanagement.dto.AuthenticationResponse;
import com.technogise.iesa.usermanagement.dto.LoginRequest;
import com.technogise.iesa.usermanagement.dto.RegisterRequest;
import com.technogise.iesa.usermanagement.security.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Register endpoint
     * Public endpoint for user self-registration
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/v1/auth/register - Registration attempt for user: {}", request.getUsername());
        AuthenticationResponse response = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/v1/auth/login - Login attempt for user: {}", request.getUsernameOrEmail());
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh token endpoint
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        log.info("POST /api/v1/auth/refresh - Refresh token request");

        // Extract refresh token from Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String refreshToken = authHeader.substring(7);
        AuthenticationResponse response = authenticationService.refreshToken(refreshToken);

        return ResponseEntity.ok(response);
    }

}
