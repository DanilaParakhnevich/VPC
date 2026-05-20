package by.parakhnevich.gateway.kafka.producer;

import by.parakhnevich.dto.request.UserRequest;
import by.parakhnevich.dto.response.UserResponse;
import com.fasterxml.jackson.databind.JsonSerializer;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
@Getter
public class UserRequestProducer {

    private static final Logger LOGGER = LogManager.getLogger(UserRequestProducer.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private final Map<String, CompletableFuture<UserResponse>> pendingRequests = new ConcurrentHashMap<>();
    private final ScheduledExecutorService timeoutScheduler = Executors.newScheduledThreadPool(10);


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

    public CompletableFuture<UserResponse> register(String email, String username, String password) {
        String requestId = UUID.randomUUID().toString();

        UserRequest request = UserRequest.builder()
                .requestId(requestId)
                .email(email)
                .username(username)
                .password(password)
                .action(UserRequest.Action.REGISTER)
                .build();

        return sendAndReceive(request);
    }

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

    public CompletableFuture<UserResponse> getUserById(UUID id) {
        String requestId = UUID.randomUUID().toString();

        UserRequest request = UserRequest.builder()
                .requestId(requestId)
                .userId(String.valueOf(id))
                .action(UserRequest.Action.GET_USER_BY_ID)
                .build();

        return sendAndReceive(request);
    }

    public CompletableFuture<UserResponse> getUserByUsername(String username) {
        String requestId = UUID.randomUUID().toString();

        UserRequest request = UserRequest.builder()
                .requestId(requestId)
                .username(username)
                .action(UserRequest.Action.GET_USER_BY_USERNAME)
                .build();

        return sendAndReceive(request);
    }

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
                        .status(UserResponse.Status.ERROR)
                        .build();

                pending.complete(timeoutResponse);
            }
        }, 5, TimeUnit.SECONDS);

        kafkaTemplate.send("users-request", request.getRequestId(), request)
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
}
