package com.mediqueue.repository;

import com.mediqueue.dto.BookingDto;
import com.mediqueue.service.FirebaseService;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TokenRepository {
    private final Map<String, List<BookingDto>> localCache = new ConcurrentHashMap<>();
    private final FirebaseService firebaseService;

    public TokenRepository(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;

        // Seed bookings for default patient MQ-2026-00417
        List<BookingDto> bookings = new ArrayList<>();
        bookings.add(new BookingDto("tok_1", "MQ-2026-00417", "hosp-1", "Aga Khan Hospital", "2026-09-10", "10:30", "10:00", "Booked"));
        bookings.add(new BookingDto("tok_2", "MQ-2026-00417", "hosp-1", "Aga Khan Hospital", "2026-08-30", "09:00", "08:30", "Completed"));
        localCache.put("MQ-2026-00417", bookings);
    }

    @SuppressWarnings("unchecked")
    public List<BookingDto> findByMqId(String mqId) {
        if (firebaseService.isFirebaseAvailable()) {
            List<BookingDto> fbData = firebaseService.readPath("patient_tokens/" + mqId, List.class);
            if (fbData != null) {
                return fbData;
            }
        }
        return localCache.getOrDefault(mqId, new ArrayList<>());
    }

    public void setBookings(String mqId, List<BookingDto> bookings) {
        localCache.put(mqId, bookings);
        if (firebaseService.isFirebaseAvailable()) {
            firebaseService.writePath("patient_tokens/" + mqId, bookings);
        }
    }

    public boolean hasOpenVisit(String mqId) {
        List<BookingDto> list = findByMqId(mqId);
        if (list == null) return false;
        for (BookingDto b : list) {
            if ("CheckedIn".equalsIgnoreCase(b.getStatus()) || "InConsultation".equalsIgnoreCase(b.getStatus())) {
                return true;
            }
        }
        return false;
    }

    public void deleteByMqId(String mqId) {
        localCache.remove(mqId);
        if (firebaseService.isFirebaseAvailable()) {
            firebaseService.removePath("patient_tokens/" + mqId);
        }
    }
}
