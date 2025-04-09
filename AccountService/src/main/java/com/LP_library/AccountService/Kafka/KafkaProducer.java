package com.LP_library.AccountService.Kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    // Constructor-based injection of KafkaTemplate
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Send message method
    public void sendMessage(String message) {
        try {
            kafkaTemplate.send(topicName, message);
            System.out.println("Sent message: " + message);
        } catch (Exception e) {
            e.printStackTrace();  // Log any error while sending the message
        }
    }
}
