package com.mediqueue.service;

import com.mediqueue.dto.BookingDto;
import com.mediqueue.dto.DeletionPathsResponse;
import com.mediqueue.dto.PatientDetailsResponse;
import com.mediqueue.exception.OpenVisitException;
import com.mediqueue.repository.AccountRepository;
import com.mediqueue.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeletionServiceTest {

    private AccountRepository accountRepository;
    private TokenRepository tokenRepository;
    private DeletionService deletionService;

    private final String UID = "uid-test-123";
    private final String MQ_ID = "MQ-2026-99999";

    @BeforeEach
    public void setup() {
        FirebaseService firebaseService = new FirebaseService();
        accountRepository = new AccountRepository(firebaseService);
        tokenRepository = new TokenRepository(firebaseService);
        deletionService = new DeletionService(accountRepository, tokenRepository);

        PatientDetailsResponse patient = new PatientDetailsResponse(
                UID, MQ_ID, "Test Patient", "0700000000", "test@example.com", "en", "Active", 0
        );
        accountRepository.save(patient);
    }

    @Test
    public void testGetDeletionPathsReturnsCorrectOrder() {
        DeletionPathsResponse pathsResponse = deletionService.getDeletionPaths(UID);
        List<String> paths = pathsResponse.getPaths();

        assertEquals(2, paths.size());
        assertEquals("patient_tokens/" + MQ_ID, paths.get(0), "First path MUST be patient_tokens/{mqId}");
        assertEquals("accounts/" + UID, paths.get(1), "Second path MUST be accounts/{uid}");
    }

    @Test
    public void testDeleteAccountOrderedDeletesTokensThenAccount() {
        tokenRepository.setBookings(MQ_ID, Collections.emptyList());

        deletionService.deleteAccountOrdered(UID);

        // Verify tokens are gone
        assertTrue(tokenRepository.findByMqId(MQ_ID).isEmpty());
        // Verify account is gone
        assertFalse(accountRepository.existsByUid(UID));
    }

    @Test
    public void testDeleteAccountThrowsOpenVisitExceptionWhenActiveVisit() {
        BookingDto activeVisit = new BookingDto("tok_active", MQ_ID, "hosp-1", "Hospital", "2026-09-05", "10:00", "09:30", "InConsultation");
        tokenRepository.setBookings(MQ_ID, Collections.singletonList(activeVisit));

        OpenVisitException ex = assertThrows(OpenVisitException.class, () -> {
            deletionService.deleteAccountOrdered(UID);
        });

        assertEquals("open_visit", ex.getCode());
        assertEquals(409, ex.getStatus().value());
    }
}
