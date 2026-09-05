package com.mediqueue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediqueue.dto.AuthRequest;
import com.mediqueue.dto.PasswordResetRequest;
import com.mediqueue.dto.RefreshRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSigninSuccess() throws Exception {
        AuthRequest request = new AuthRequest("patient@example.com", "password123");
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    public void testPasswordResetReturns204EvenIfUnknownEmail() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest("unknown@example.com");
        mockMvc.perform(post("/api/auth/password-reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testRefreshTokenSuccess() throws Exception {
        RefreshRequest request = new RefreshRequest("valid-refresh-token");
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idToken").exists())
                .andExpect(jsonPath("$.userId").exists());
    }
}
