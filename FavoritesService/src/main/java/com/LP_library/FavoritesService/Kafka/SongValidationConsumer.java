package com.LP_library.FavoritesService.Kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SongValidationConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, CompletableFuture<Boolean>> pendingRequests = new ConcurrentHashMap<>();

    public CompletableFuture<Boolean> registerValidationRequest(String correlationId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);
        return future;
    }

    @KafkaListener(topics = "song-validate-response", groupId = "favorites")
    public void consumeValidationResponse(String message) throws JsonProcessingException {
        JsonNode json = objectMapper.readTree(message);
        String correlationId = json.get("correlationId").asText();
        boolean exists = json.get("exists").asBoolean();

        CompletableFuture<Boolean> future = pendingRequests.remove(correlationId);
        if (future != null) {
            future.complete(exists);
        }
    }
}

