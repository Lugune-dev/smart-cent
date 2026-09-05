package com.mediqueue.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StreamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSseStreamInitialPutEvent() throws Exception {
        mockMvc.perform(get("/api/stream/patient_tokens?auth=valid-token")
                .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM));
    }

    @Test
    public void testSseStreamAuthRevokedOnInvalidToken() throws Exception {
        mockMvc.perform(get("/api/stream/patient_tokens?auth=invalid-token")
                .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("event:auth_revoked")));
    }

    @Test
    public void testPollingFallbackSuccess() throws Exception {
        mockMvc.perform(get("/api/poll/patient_tokens?auth=valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entity").value("patient_tokens"))
                .andExpect(jsonPath("$.data").isArray());
    }
}
