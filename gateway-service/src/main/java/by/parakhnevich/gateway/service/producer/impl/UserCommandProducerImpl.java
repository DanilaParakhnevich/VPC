package by.parakhnevich.gateway.service.producer.impl;

import by.parakhnevich.dto.request.UserRequest;
import by.parakhnevich.dto.response.UserResponse;
import by.parakhnevich.gateway.service.producer.UserCommandProducer;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by agallochum on 2026-05-15
 */
@Service
public class UserCommandProducerImpl implements UserCommandProducer {

    private static final Logger LOGGER = LogManager.getLogger(UserCommandProducerImpl.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.user-request}")
    private String requestTopic;

    @Value("${kafka.topics.user-response}")
    private String responseTopic;

    private final Map<String, CompletableFuture<UserResponse>> pendingRequests = new ConcurrentHashMap<>();
    private final ScheduledExecutorService timeoutScheduler = Executors.newScheduledThreadPool(10);

    @Override
    public CompletableFuture<UserResponse> validateToken(String token) {
        String requestId = UUID.randomUUID().toString();

        UserRequest request = UserRequest.builder()
                .requestId(requestId)
                .token(token)
                .action(UserRequest.Action.VALIDATE)
                .dateTime(ZonedDateTime.now())
                .build();

        return sendAndReceive(request);
    }

    @Override
    public CompletableFuture<UserResponse> authenticate(String username, String password) {
        String requestId = UUID.randomUUID().toString();

        UserRequest request = UserRequest.builder()
                .requestId(requestId)
                .username(username)
                .password(password)
                .action(UserRequest.Action.AUTHENTICATE)
                .build();

        return sendAndReceive(request);
    }

    @Override
    public CompletableFuture<UserResponse> getUserById(UUID id) {
        String requestId = UUID.randomUUID().toString();

        UserRequest request = UserRequest.builder()
                .requestId(requestId)
                .userId(String.valueOf(id))
                .action(UserRequest.Action.GET_USER_BY_ID)
                .build();

        return sendAndReceive(request);
    }

    @Override
    public CompletableFuture<UserResponse> getUserByUsername(String username) {
        String requestId = UUID.randomUUID().toString();

        UserRequest request = UserRequest.builder()
                .requestId(requestId)
                .username(username)
                .action(UserRequest.Action.GET_USER_BY_USERNAME)
                .build();

        return sendAndReceive(request);
    }

    @Override
    public CompletableFuture<UserResponse> updateUser(UUID userId, Map<String, Object> updates) {
        String requestId = UUID.randomUUID().toString();

        UserRequest request = UserRequest.builder()
                .requestId(requestId)
                .userId(userId.toString())
                .updates(updates)
                .action(UserRequest.Action.UPDATE_USER)
                .build();

        return sendAndReceive(request);
    }

    private CompletableFuture<UserResponse> sendAndReceive(UserRequest request) {
        CompletableFuture<UserResponse> future = new CompletableFuture<>();

        pendingRequests.put(request.getRequestId(), future);

        timeoutScheduler.schedule(() -> {
            CompletableFuture<UserResponse> pending = pendingRequests.remove(request.getRequestId());
            if (pending != null && !pending.isDone()) {
                LOGGER.warn("Request timeout: {}", request.getRequestId());

                UserResponse timeoutResponse = UserResponse.builder()
                        .requestId(request.getRequestId())
                        .errorMessage(UserResponse.ErrorMessage.TIMEOUT)
                        .status(UserResponse.Status.VALIDATION_ERROR)
                        .build();

                pending.complete(timeoutResponse);
            }
        }, 5, TimeUnit.SECONDS);

        kafkaTemplate.send(requestTopic, request.getRequestId(), request)
                .whenComplete((result, e) -> {
                    if (e != null) {
                        LOGGER.error("Failed to send Kafka message: {}", request.getRequestId(), e);
                        CompletableFuture<UserResponse> pending = pendingRequests.remove(request.getRequestId());
                        if (pending != null && !pending.isDone()) {
                            pending.completeExceptionally(e);
                        }
                    } else {
                        LOGGER.debug("Sent Kafka message: {} to partition {}",
                                request.getRequestId(),
                                result.getRecordMetadata().partition());
                    }
                });

        return future;
    }

    @KafkaListener(
            topics = "${kafka.topics.user-response}",
            groupId = "gateway-group"
    )
    public void handleResponse(UserResponse response) {
        LOGGER.info("Received response for request: {}, success: {}",
                response.getRequestId(),
                response.getErrorMessage().equals(UserResponse.ErrorMessage.NONE));

        CompletableFuture<UserResponse> future = pendingRequests.remove(response.getRequestId());

        if (future != null) {
            future.complete(response);
        } else {
            LOGGER.warn("No pending request found for response: {}", response.getRequestId());
        }
    }
}
