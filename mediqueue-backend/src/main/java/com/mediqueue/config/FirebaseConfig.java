package com.mediqueue.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.config-path:}")
    private String configPath;

    @Value("${firebase.database-url:https://mediqueue-default-rtdb.firebaseio.com}")
    private String databaseUrl;

    @PostConstruct
    public void initialize() {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        try {
            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                    .setDatabaseUrl(databaseUrl);

            if (configPath != null && !configPath.trim().isEmpty()) {
                InputStream serviceAccount = new FileInputStream(configPath);
                optionsBuilder.setCredentials(GoogleCredentials.fromStream(serviceAccount));
            } else {
                try {
                    optionsBuilder.setCredentials(GoogleCredentials.getApplicationDefault());
                } catch (Exception ex) {
                    logger.warn("No application default credentials found, running without GoogleCredentials");
                }
            }

            FirebaseApp.initializeApp(optionsBuilder.build());
            logger.info("Firebase App initialized successfully");
        } catch (Exception e) {
            logger.warn("Firebase initialized in fallback/offline mode: {}", e.getMessage());
        }
    }
}
