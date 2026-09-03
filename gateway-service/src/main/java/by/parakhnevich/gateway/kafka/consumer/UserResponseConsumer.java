package by.parakhnevich.gateway.kafka.consumer;

import by.parakhnevich.dto.response.user.UserResponse;
import by.parakhnevich.gateway.kafka.producer.UserRequestProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Created by agallochum on 2026-05-20
 */
@Component
@AllArgsConstructor
public class UserResponseConsumer {

    private static final Logger LOGGER = LogManager.getLogger(UserResponseConsumer.class);

    private UserRequestProducer userRequestProducer;

    private ObjectMapper objectMapper;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 2000)
    )
    @KafkaListener(
            topics = "users-response",
            groupId = "gateway"
    )
    public void handleResponse(String responseStr) {
        try {
            var response = objectMapper.readValue(responseStr, UserResponse.class);

            LOGGER.info(response.toString());
            LOGGER.info("Received response for request: {}, success: {}",
                    response.getRequestId(),
                    response.getErrorMessage().equals(UserResponse.ErrorMessage.NONE));

            CompletableFuture<UserResponse> future = userRequestProducer.getPendingRequests().remove(response.getRequestId());

            if (future != null) {
                future.complete(response);
            } else {
                LOGGER.warn("No pending request found for response: {}", response.getRequestId());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
