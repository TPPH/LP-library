package com.LP_library.FavoritesService.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class KafkaSongValidator {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, CompletableFuture<Boolean>> pendingRequests = new ConcurrentHashMap<>();

    private static final String REQUEST_TOPIC = "song-validate-request";
    private static final String REPLY_TOPIC = "song-validate-response";

    public boolean validateSongId(Long songId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        try {
            String payload = objectMapper.writeValueAsString(Map.of(
                    "songId", songId,
                    "correlationId", correlationId
            ));

            System.out.println("📤 [Favorites] Sending validation request: " + payload);
            kafkaTemplate.send(REQUEST_TOPIC, payload);

            // Wait up to 5 seconds for response
            Boolean result = future.get(5, TimeUnit.SECONDS);
            System.out.println("✅ [Favorites] Received validation result for songId " + songId + ": " + result);
            return result;

        } catch (TimeoutException e) {
            System.out.println("⏰ [Favorites] Timeout waiting for validation response for songId: " + songId);
        } catch (Exception e) {
            System.out.println("❌ [Favorites] Error validating songId: " + songId + " - " + e.getMessage());
        } finally {
            pendingRequests.remove(correlationId);
        }

        return false;
    }

    @KafkaListener(topics = REPLY_TOPIC, groupId = "favorites")
    public void consumeValidationResponse(String message) {
        System.out.println("🔥 [Favorites] Received response from Kafka: " + message);
        try {
            JsonNode json = objectMapper.readTree(message);
            String correlationId = json.get("correlationId").asText();
            boolean exists = json.get("exists").asBoolean();

            CompletableFuture<Boolean> future = pendingRequests.remove(correlationId);
            if (future != null) {
                future.complete(exists);
                System.out.println("🔄 [Favorites] Completed future for correlationId: " + correlationId + ", exists: " + exists);
            } else {
                System.out.println("⚠️ [Favorites] No matching future found for correlationId: " + correlationId);
            }
        } catch (Exception e) {
            System.out.println("💥 [Favorites] Error while processing Kafka response: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
