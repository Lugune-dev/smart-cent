package com.mediqueue.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseService {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseService.class);

    public boolean isFirebaseAvailable() {
        return !FirebaseApp.getApps().isEmpty();
    }

    public <T> T readPath(String path, Class<T> valueType) {
        if (!isFirebaseAvailable()) {
            return null;
        }
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
            CompletableFuture<T> future = new CompletableFuture<>();
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    future.complete(snapshot.getValue(valueType));
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
            return future.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("Firebase read at {} failed or timed out: {}", path, e.getMessage());
            return null;
        }
    }

    public void writePath(String path, Object data) {
        if (!isFirebaseAvailable()) {
            return;
        }
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
            CompletableFuture<Void> future = new CompletableFuture<>();
            ref.setValue(data, (error, reference) -> {
                if (error != null) {
                    future.completeExceptionally(error.toException());
                } else {
                    future.complete(null);
                }
            });
            future.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("Firebase write at {} failed or timed out: {}", path, e.getMessage());
        }
    }

    public void removePath(String path) {
        if (!isFirebaseAvailable()) {
            return;
        }
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
            CompletableFuture<Void> future = new CompletableFuture<>();
            ref.removeValue((error, reference) -> {
                if (error != null) {
                    future.completeExceptionally(error.toException());
                } else {
                    future.complete(null);
                }
            });
            future.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("Firebase remove at {} failed or timed out: {}", path, e.getMessage());
        }
    }
}
