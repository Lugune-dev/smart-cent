package com.mediqueue.service;

import com.mediqueue.dto.BookingDto;
import com.mediqueue.dto.PatientBookingsResponse;
import com.mediqueue.dto.PatientDetailsResponse;
import com.mediqueue.repository.AccountRepository;
import com.mediqueue.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PatientService {

    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;

    public PatientService(AccountRepository accountRepository, TokenRepository tokenRepository) {
        this.accountRepository = accountRepository;
        this.tokenRepository = tokenRepository;
    }

    public PatientDetailsResponse getPatientDetails(String uid, String lang) {
        PatientDetailsResponse details = accountRepository.findByUid(uid);
        if (lang != null && !lang.isBlank()) {
            details.setLanguage(lang);
        }
        return details;
    }

    public PatientBookingsResponse getPatientBookings(String uid, String lang) {
        PatientDetailsResponse patient = accountRepository.findByUid(uid);
        String mqId = patient.getMqId();
        List<BookingDto> bookings = tokenRepository.findByMqId(mqId);

        // Sort newest first
        Collections.reverse(bookings);

        return new PatientBookingsResponse(mqId, bookings);
    }

    public String generateDataDownload(String uid, String lang) {
        PatientDetailsResponse patient = accountRepository.findByUid(uid);
        List<BookingDto> bookings = tokenRepository.findByMqId(patient.getMqId());

        boolean isSwahili = "sw".equalsIgnoreCase(lang);

        StringBuilder sb = new StringBuilder();
        sb.append(isSwahili ? "=== REKODI ZA MGONJWA (MEDIQUEUE) ===" : "=== MEDIQUEUE PATIENT EXPORT DATA ===").append("\n\n");
        sb.append(isSwahili ? "Namba ya Mgonjwa (MQ ID): " : "Patient MQ ID: ").append(patient.getMqId()).append("\n");
        sb.append(isSwahili ? "Jina: " : "Name: ").append(patient.getName()).append("\n");
        sb.append(isSwahili ? "Simu: " : "Phone: ").append(patient.getPhone()).append("\n");
        sb.append(isSwahili ? "Barua pepe: " : "Email: ").append(patient.getEmail()).append("\n");
        sb.append(isSwahili ? "Hali ya Bima: " : "Insurance Status: ").append(patient.getInsuranceStatus()).append("\n");
        sb.append(isSwahili ? "Idadi ya Wategemezi: " : "Dependants Count: ").append(patient.getDependantsCount()).append("\n\n");

        sb.append(isSwahili ? "--- NAfASI NA APPOINTMENT ZANGU ---" : "--- MY QUEUE TOKENS & APPOINTMENTS ---").append("\n");
        if (bookings.isEmpty()) {
            sb.append(isSwahili ? "Hakuna miadi iliyopatikana." : "No bookings found.").append("\n");
        } else {
            for (BookingDto b : bookings) {
                sb.append("- [").append(b.getAppointmentDate()).append("] ")
                  .append(b.getHospitalName())
                  .append(" | Doctor: ").append(b.getDoctorTime())
                  .append(" | Arrive: ").append(b.getArriveByTime())
                  .append(" | Status: ").append(b.getStatus()).append("\n");
            }
        }

        sb.append("\n").append(isSwahili ? "Taarifa hizi zimetolewa chini ya Sheria ya Hifadhi ya Data (PDPC Act)." : "This export is provided under the Tanzania Personal Data Protection Commission (PDPC) Act.");
        return sb.toString();
    }
}
