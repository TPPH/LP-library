package com.LP_library.FavoritesService.Kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SongValidationProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(SongValidationConsumer.class);

    public void sendValidationRequest(Long songId, String correlationId) {
        Map<String, Object> message = Map.of(
                "songId", songId,
                "correlationId", correlationId
        );

        try {
            kafkaTemplate.send("song-validate-request", objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize validation request for songId: {} and correlationId: {}", songId, correlationId, e);
            throw new RuntimeException("Failed to serialize validation request", e); // You could throw a custom exception here
        } catch (Exception e) {
            logger.error("Unexpected error while sending validation request", e);
            throw new RuntimeException("Unexpected error while sending validation request", e); // You can also throw a custom exception
        }
    }

}

