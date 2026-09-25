package by.parakhnevich.gateway.kafka.consumer;

import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.gateway.kafka.producer.MusicRequestProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

/**
 * Created by agallochum on 2026-09-25
 */
@Component
@AllArgsConstructor
public class MusicResponseConsumer {

    private static final Logger LOGGER = LogManager.getLogger(MusicResponseConsumer.class);

    private final MusicRequestProducer musicRequestProducer;
    private final ObjectMapper objectMapper;

    @RetryableTopic(attempts = "3", backOff = @BackOff(delay = 2000))
    @KafkaListener(topics = "music-response")
    public void handleResponse(String responseStr) {
        try {
            var response = objectMapper.readValue(responseStr, MusicResponse.class);
            LOGGER.info("Received response for request: {}, success: {}",
                    response.requestId(), response.isSuccess());

            var future = musicRequestProducer.getPendingRequests().remove(response.requestId());
            if (future != null) {
                future.complete(response);
            } else {
                LOGGER.warn("No pending request found for response: {}", response.requestId());
            }
        } catch (Exception e) {
            LOGGER.error("Error processing response: {}", e.getMessage(), e);
        }
    }
}