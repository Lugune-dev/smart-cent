package com.mediqueue.controller;

import com.mediqueue.dto.DeletionPathsResponse;
import com.mediqueue.dto.PatientBookingsResponse;
import com.mediqueue.dto.PatientDetailsResponse;
import com.mediqueue.service.DeletionService;
import com.mediqueue.service.PatientService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final PatientService patientService;
    private final DeletionService deletionService;

    public PatientController(PatientService patientService, DeletionService deletionService) {
        this.patientService = patientService;
        this.deletionService = deletionService;
    }

    @GetMapping("/me")
    public ResponseEntity<PatientDetailsResponse> getDetails(@RequestParam(defaultValue = "en") String lang, Authentication auth) {
        String uid = (String) auth.getPrincipal();
        return ResponseEntity.ok(patientService.getPatientDetails(uid, lang));
    }

    @GetMapping("/bookings")
    public ResponseEntity<PatientBookingsResponse> getBookings(@RequestParam(defaultValue = "en") String lang, Authentication auth) {
        String uid = (String) auth.getPrincipal();
        return ResponseEntity.ok(patientService.getPatientBookings(uid, lang));
    }

    @GetMapping("/download")
    public ResponseEntity<String> downloadData(@RequestParam(defaultValue = "en") String lang, Authentication auth) {
        String uid = (String) auth.getPrincipal();
        String textExport = patientService.generateDataDownload(uid, lang);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "my-data.txt");

        return ResponseEntity.ok().headers(headers).body(textExport);
    }

    @GetMapping("/deletion-paths")
    public ResponseEntity<DeletionPathsResponse> getDeletionPaths(Authentication auth) {
        String uid = (String) auth.getPrincipal();
        return ResponseEntity.ok(deletionService.getDeletionPaths(uid));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount(Authentication auth) {
        String uid = (String) auth.getPrincipal();
        deletionService.deleteAccountOrdered(uid);
        return ResponseEntity.noContent().build();
    }
}
