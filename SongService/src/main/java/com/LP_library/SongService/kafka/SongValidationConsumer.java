package com.LP_library.SongService.kafka;

import com.LP_library.SongService.repository.SongRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SongValidationConsumer {

    private final SongRepository songRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(SongValidationConsumer.class);

    @KafkaListener(topics = "song-validate-request", groupId = "song")
    public void consumeValidationRequest(String message) {
        System.out.println("🔥 [Kafka] Received message: " + message);

        try {
            JsonNode json = objectMapper.readTree(message);
            Long songId = json.get("songId").asLong();
            String correlationId = json.get("correlationId").asText();

            logger.info("Received validation request for songId: {} with correlationId: {}", songId, correlationId);
            System.out.println("🔍 Validating songId: " + songId + ", correlationId: " + correlationId);

            boolean exists = songRepository.existsById(songId);
            System.out.println("✅ Song exists: " + exists);

            Map<String, Object> response = Map.of(
                    "songId", songId,
                    "exists", exists,
                    "correlationId", correlationId
            );

            String responseJson = objectMapper.writeValueAsString(response);
            kafkaTemplate.send("song-validate-response", responseJson);

            logger.info("Sent validation response for songId: {} with exists: {}", songId, exists);
            System.out.println("📤 Sent response: " + responseJson);

        } catch (JsonProcessingException e) {
            logger.error("Error processing the validation request: {}", message, e);
            System.out.println("❌ JSON processing error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while processing validation request", e);
            System.out.println("💥 Unexpected error: " + e.getMessage());
        }
    }
}
