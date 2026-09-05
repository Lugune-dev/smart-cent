package com.mediqueue.service;

import com.mediqueue.dto.*;
import com.mediqueue.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthResponse signin(AuthRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "invalid_input", "Email and password are required.");
        }

        String localId = "user-" + Math.abs(request.getEmail().hashCode());
        String idToken = "mock-token-" + localId;
        String refreshToken = "mock-refresh-" + UUID.randomUUID().toString();

        return new AuthResponse(idToken, refreshToken, "3600", localId);
    }

    public AuthResponse signup(AuthRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "invalid_input", "Email and password are required.");
        }

        String localId = "user-" + Math.abs(request.getEmail().hashCode());
        String idToken = "mock-token-" + localId;
        String refreshToken = "mock-refresh-" + UUID.randomUUID().toString();

        return new AuthResponse(idToken, refreshToken, "3600", localId);
    }

    public void resetPassword(PasswordResetRequest request) {
        // Privacy-Preserving Password Reset: Always completes with 204 No Content even if EMAIL_NOT_FOUND
        logger.info("Password reset requested for email: {}", request != null ? request.getEmail() : "null");
    }

    public RefreshResponse refreshToken(RefreshRequest request) {
        if (request == null || request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "invalid_input", "Refresh token is required.");
        }

        String userId = "user-refreshed";
        String newIdToken = "mock-token-" + userId;
        String newRefreshToken = "mock-refresh-" + UUID.randomUUID().toString();

        return new RefreshResponse(newIdToken, newRefreshToken, "3600", userId);
    }
}
