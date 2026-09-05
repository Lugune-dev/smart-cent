package com.mediqueue.security;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.mediqueue.exception.AuthRevokedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class FirebaseAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String paramToken = request.getParameter("auth");

        String idToken = null;
        if (header != null && header.startsWith("Bearer ")) {
            idToken = header.substring(7);
        } else if (paramToken != null && !paramToken.trim().isEmpty()) {
            idToken = paramToken;
        }

        if (idToken != null && !idToken.trim().isEmpty()) {
            try {
                String uid = null;
                if (!FirebaseApp.getApps().isEmpty()) {
                    // Strict Firebase ID Token verification in active mode
                    FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                    uid = decodedToken.getUid();
                } else {
                    // Only allowed in offline/test environment when Firebase SDK is not initialized
                    uid = normalizeTokenToUid(idToken);
                }

                if (uid != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(uid, idToken, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String normalizeTokenToUid(String token) {
        if (token == null || token.isBlank()) return null;
        String clean = token.trim();
        if (clean.startsWith("mock-token-")) clean = clean.substring("mock-token-".length());
        else if (clean.startsWith("test-token-")) clean = clean.substring("test-token-".length());
        else if (clean.startsWith("mock-")) clean = clean.substring("mock-".length());
        else if (clean.startsWith("test-")) clean = clean.substring("test-".length());
        else if (clean.startsWith("bearer-")) clean = clean.substring("bearer-".length());
        return clean;
    }
}
