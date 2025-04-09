package com.LP_library.AccountService.Kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "myTopic", groupId = "myGroup")
    public void consume(String message) {
        System.out.println("Received message: " + message);
    }
}
