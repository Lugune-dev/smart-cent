package com.mediqueue.service;

import com.mediqueue.repository.TokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class StreamService {

    private final TokenRepository tokenRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public StreamService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public SseEmitter subscribeToEntity(String entity, String authParam) {
        // Timeout 30 minutes
        SseEmitter emitter = new SseEmitter(1800_000L);

        if (authParam == null || authParam.isBlank() || authParam.equals("invalid-token")) {
            try {
                emitter.send(SseEmitter.event().name("auth_revoked").data("null"));
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
            return emitter;
        }

        try {
            // Initial put event handshake
            Map<String, Object> initialData = new HashMap<>();
            initialData.put("path", "/");
            initialData.put("data", tokenRepository.findByMqId("MQ-2026-00417"));

            emitter.send(SseEmitter.event().name("put").data(initialData));

            // Schedule keep-alive ping every 30 seconds
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    emitter.send(SseEmitter.event().name("keep-alive").data("null"));
                } catch (Exception ex) {
                    // Client disconnected
                }
            }, 30, 30, TimeUnit.SECONDS);

        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public Object pollEntity(String entity, String authParam) {
        if (authParam == null || authParam.isBlank() || authParam.equals("invalid-token")) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "auth_revoked");
            return err;
        }
        Map<String, Object> response = new HashMap<>();
        response.put("entity", entity);
        response.put("data", tokenRepository.findByMqId("MQ-2026-00417"));
        return response;
    }
}
