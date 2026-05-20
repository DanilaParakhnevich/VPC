package by.parakhnevich.gateway.kafka.consumer;

import by.parakhnevich.dto.response.UserResponse;
import by.parakhnevich.gateway.kafka.producer.UserRequestProducer;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Created by agallochum on 2026-05-20
 */
@Component
@AllArgsConstructor
public class UserRequestConsumer {

    private static final Logger LOGGER = LogManager.getLogger(UserRequestConsumer.class);

    private UserRequestProducer userRequestProducer;

    @KafkaListener(
            topics = "users-response",
            groupId = "gateway"
    )
    public void handleResponse(String response) {
        LOGGER.info(response.toString());
        System.out.println(response);
        LOGGER.info("Received response for request: {}, success: {}",
                response.getRequestId(),
                response.getErrorMessage().equals(UserResponse.ErrorMessage.NONE));

        CompletableFuture<UserResponse> future = userRequestProducer.getPendingRequests().remove(response.getRequestId());

        if (future != null) {
            future.complete(response);
        } else {
            LOGGER.warn("No pending request found for response: {}", response.getRequestId());
        }
    }

}
