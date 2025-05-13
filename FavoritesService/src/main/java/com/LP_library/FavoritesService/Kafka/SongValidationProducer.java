package com.LP_library.FavoritesService.Kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SongValidationProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendValidationRequest(Long songId, String correlationId) {
        Map<String, Object> message = Map.of(
                "songId", songId,
                "correlationId", correlationId
        );

        try {
            kafkaTemplate.send("song-validate-request", objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize validation request", e);
        }
    }
}
