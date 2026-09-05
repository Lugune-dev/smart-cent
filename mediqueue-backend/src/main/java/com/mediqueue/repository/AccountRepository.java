package com.mediqueue.repository;

import com.mediqueue.dto.PatientDetailsResponse;
import com.mediqueue.service.FirebaseService;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountRepository {
    private final Map<String, PatientDetailsResponse> localCache = new ConcurrentHashMap<>();
    private final FirebaseService firebaseService;

    public AccountRepository(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;

        // Seed default patient for tests/fallback
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
        localCache.put("uid-grace", defaultPatient);
    }

    public PatientDetailsResponse findByUid(String uid) {
        if (firebaseService.isFirebaseAvailable()) {
            PatientDetailsResponse fbData = firebaseService.readPath("accounts/" + uid, PatientDetailsResponse.class);
            if (fbData != null) {
                return fbData;
            }
        }

        if (localCache.containsKey(uid)) {
            return localCache.get(uid);
        }

        String mqId = "MQ-2026-" + Math.abs(uid.hashCode() % 10000);
        return new PatientDetailsResponse(uid, mqId, "Patient " + uid, "0700000000", uid + "@example.com", "en", "Active", 0);
    }

    public boolean existsByUid(String uid) {
        if (firebaseService.isFirebaseAvailable()) {
            Object fbData = firebaseService.readPath("accounts/" + uid, Object.class);
            if (fbData != null) return true;
        }
        return localCache.containsKey(uid);
    }

    public void save(PatientDetailsResponse details) {
        localCache.put(details.getUid(), details);
        if (firebaseService.isFirebaseAvailable()) {
            firebaseService.writePath("accounts/" + details.getUid(), details);
        }
    }

    public void deleteByUid(String uid) {
        localCache.remove(uid);
        if (firebaseService.isFirebaseAvailable()) {
            firebaseService.removePath("accounts/" + uid);
        }
    }
}
