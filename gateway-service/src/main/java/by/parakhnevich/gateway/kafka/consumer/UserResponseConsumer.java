package by.parakhnevich.gateway.kafka.consumer;

import by.parakhnevich.common.dto.response.user.UserResponse;
import by.parakhnevich.gateway.kafka.producer.UserRequestProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

/**
 * Created by agallochum on 2026-05-20
 */
@Component
@AllArgsConstructor
public class UserResponseConsumer {

    private static final Logger LOGGER = LogManager.getLogger(UserResponseConsumer.class);

    private final UserRequestProducer userRequestProducer;
    private final ObjectMapper objectMapper;

    @RetryableTopic(attempts = "3", backOff = @BackOff(delay = 2000))
    @KafkaListener(topics = "users-response")
    public void handleResponse(String responseStr) {
        try {
            var response = objectMapper.readValue(responseStr, UserResponse.class);
            LOGGER.info("Received response for request: {}, success: {}",
                    response.requestId(), response.isSuccess());

            var future = userRequestProducer.getPendingRequests().remove(response.requestId());
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