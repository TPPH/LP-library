package com.LP_library.SongService.kafka;

import com.LP_library.SongService.repository.SongRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SongValidationConsumer {

    private final SongRepository songRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "song-validate-request", groupId = "song")
    public void consumeValidationRequest(String message) throws JsonProcessingException {
        JsonNode json = objectMapper.readTree(message);
        Long songId = json.get("songId").asLong();
        String correlationId = json.get("correlationId").asText();

        boolean exists = songRepository.existsById(songId);

        Map<String, Object> response = Map.of(
                "songId", songId,
                "exists", exists,
                "correlationId", correlationId
        );

        kafkaTemplate.send("song-validate-response", objectMapper.writeValueAsString(response));
    }
}

