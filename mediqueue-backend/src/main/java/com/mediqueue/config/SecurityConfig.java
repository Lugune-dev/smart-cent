package com.mediqueue.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediqueue.security.FirebaseAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                Map<String, Object> body = new HashMap<>();
                body.put("timestamp", Instant.now().toString());
                body.put("status", 401);
                body.put("error", "Unauthorized");
                body.put("code", "auth_revoked");
                body.put("message", "Firebase ID token is invalid, expired, or missing.");
                body.put("path", request.getRequestURI());

                response.getWriter().write(objectMapper.writeValueAsString(body));
            }))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/stream/**", "/api/poll/**", "/error").permitAll()
                .requestMatchers("/api/patient/**").authenticated()
                .anyRequest().permitAll()
            )
            .addFilterBefore(new FirebaseAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
