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
        try {
            JsonNode json = objectMapper.readTree(message);
            Long songId = json.get("songId").asLong();
            String correlationId = json.get("correlationId").asText();

            logger.info("Received validation request for songId: {} with correlationId: {}", songId, correlationId);

            boolean exists = songRepository.existsById(songId);

            Map<String, Object> response = Map.of(
                    "songId", songId,
                    "exists", exists,
                    "correlationId", correlationId
            );

            kafkaTemplate.send("song-validate-response", objectMapper.writeValueAsString(response));
            logger.info("Sent validation response for songId: {} with exists: {}", songId, exists);
        } catch (JsonProcessingException e) {
            logger.error("Error processing the validation request: {}", message, e);
        } catch (Exception e) {
            logger.error("Unexpected error while processing validation request", e);
        }
    }
}


