package com.mediqueue.service;

import com.mediqueue.dto.DeletionPathsResponse;
import com.mediqueue.dto.PatientDetailsResponse;
import com.mediqueue.exception.OpenVisitException;
import com.mediqueue.repository.AccountRepository;
import com.mediqueue.repository.TokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DeletionService {
    private static final Logger logger = LoggerFactory.getLogger(DeletionService.class);

    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;

    public DeletionService(AccountRepository accountRepository, TokenRepository tokenRepository) {
        this.accountRepository = accountRepository;
        this.tokenRepository = tokenRepository;
    }

    public DeletionPathsResponse getDeletionPaths(String uid) {
        PatientDetailsResponse patient = accountRepository.findByUid(uid);
        String mqId = patient.getMqId();
        List<String> paths = Arrays.asList("patient_tokens/" + mqId, "accounts/" + uid);
        return new DeletionPathsResponse(paths);
    }

    public void deleteAccountOrdered(String uid) {
        PatientDetailsResponse patient = accountRepository.findByUid(uid);
        String mqId = patient.getMqId();

        if (tokenRepository.hasOpenVisit(mqId)) {
            logger.warn("Account deletion blocked for UID {}: active/open visit detected.", uid);
            throw new OpenVisitException();
        }

        // STRICT DELETION SEQUENCE ORDER:
        // Step 1: Delete patient_tokens/{mqId} FIRST
        logger.info("Executing deletion Step 1: patient_tokens/{}", mqId);
        tokenRepository.deleteByMqId(mqId);

        // Step 2: Delete accounts/{uid} SECOND
        logger.info("Executing deletion Step 2: accounts/{}", uid);
        accountRepository.deleteByUid(uid);

        logger.info("Account deletion completed successfully for UID {}", uid);
    }
}
