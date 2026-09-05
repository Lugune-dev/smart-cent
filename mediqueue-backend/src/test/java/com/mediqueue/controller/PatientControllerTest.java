package com.mediqueue.controller;

import com.mediqueue.dto.BookingDto;
import com.mediqueue.dto.PatientDetailsResponse;
import com.mediqueue.repository.AccountRepository;
import com.mediqueue.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @BeforeEach
    public void setup() {
        PatientDetailsResponse defaultPatient = new PatientDetailsResponse(
                "uid-grace",
                "MQ-2026-00417",
                "Grace Mussa",
                "0712000000",
                "grace@example.com",
                "en",
                "Active",
                1
        );
        accountRepository.save(defaultPatient);

        List<BookingDto> bookings = new ArrayList<>();
        bookings.add(new BookingDto("tok_1", "MQ-2026-00417", "hosp-1", "Aga Khan Hospital", "2026-09-10", "10:30", "10:00", "Booked"));
        bookings.add(new BookingDto("tok_2", "MQ-2026-00417", "hosp-1", "Aga Khan Hospital", "2026-08-30", "09:00", "08:30", "Completed"));
        tokenRepository.setBookings("MQ-2026-00417", bookings);
    }

    @Test
    public void testGetPatientMeAuthenticated() throws Exception {
        mockMvc.perform(get("/api/patient/me")
                .header("Authorization", "Bearer mock-uid-grace"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("uid-grace"))
                .andExpect(jsonPath("$.mqId").value("MQ-2026-00417"));
    }

    @Test
    public void testGetPatientMeUnauthenticatedReturns401WithRfc7807() throws Exception {
        mockMvc.perform(get("/api/patient/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("auth_revoked"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    public void testGetBookings() throws Exception {
        mockMvc.perform(get("/api/patient/bookings")
                .header("Authorization", "Bearer mock-uid-grace"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mqId").value("MQ-2026-00417"))
                .andExpect(jsonPath("$.bookings").isArray());
    }

    @Test
    public void testDownloadDataAttachment() throws Exception {
        mockMvc.perform(get("/api/patient/download?lang=sw")
                .header("Authorization", "Bearer mock-uid-grace"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"my-data.txt\""))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("MQ ID")));
    }

    @Test
    public void testGetDeletionPathsOrder() throws Exception {
        mockMvc.perform(get("/api/patient/deletion-paths")
                .header("Authorization", "Bearer mock-uid-grace"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths[0]").value("patient_tokens/MQ-2026-00417"))
                .andExpect(jsonPath("$.paths[1]").value("accounts/uid-grace"));
    }

    @Test
    public void testDeleteAccountSuccessWhenNoOpenVisit() throws Exception {
        mockMvc.perform(delete("/api/patient/delete")
                .header("Authorization", "Bearer mock-uid-grace"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteAccountFails409WhenOpenVisit() throws Exception {
        BookingDto openVisit = new BookingDto("tok_active", "MQ-2026-00417", "hosp-1", "Aga Khan Hospital", "2026-09-05", "11:00", "10:30", "CheckedIn");
        tokenRepository.setBookings("MQ-2026-00417", Collections.singletonList(openVisit));

        mockMvc.perform(delete("/api/patient/delete")
                .header("Authorization", "Bearer mock-uid-grace"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("open_visit"))
                .andExpect(jsonPath("$.status").value(409));
    }
}
