package by.parakhnevich.gateway.kafka.producer;

import by.parakhnevich.dto.request.user.AuthRequest;
import by.parakhnevich.dto.response.user.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, CompletableFuture<UserResponse>> pendingRequests = new ConcurrentHashMap<>();
    private final ScheduledExecutorService timeoutScheduler = Executors.newScheduledThreadPool(10);

    public CompletableFuture<UserResponse> register(String email, String username, String password) throws JsonProcessingException {
        String requestId = UUID.randomUUID().toString();
        AuthRequest request = AuthRequest.builder()
                .requestId(requestId)
                .email(email)
                .username(username)
                .password(password)
                .action(AuthRequest.Action.REGISTER)
                .build();
        return sendAndReceive(request);
    }

    public CompletableFuture<UserResponse> authenticate(String username, String password) throws JsonProcessingException {
        String requestId = UUID.randomUUID().toString();
        AuthRequest request = AuthRequest.builder()
                .requestId(requestId)
                .username(username)
                .password(password)
                .action(AuthRequest.Action.AUTHENTICATE)
                .build();
        return sendAndReceive(request);
    }

    public CompletableFuture<UserResponse> getUserById(Long id) throws JsonProcessingException {
        String requestId = UUID.randomUUID().toString();
        AuthRequest request = AuthRequest.builder()
                .requestId(requestId)
                .userId(String.valueOf(id))
                .action(AuthRequest.Action.GET_USER_BY_ID)
                .build();
        return sendAndReceive(request);
    }

    public CompletableFuture<UserResponse> getUserByUsername(String username) throws JsonProcessingException {
        String requestId = UUID.randomUUID().toString();
        AuthRequest request = AuthRequest.builder()
                .requestId(requestId)
                .username(username)
                .action(AuthRequest.Action.GET_USER_BY_USERNAME)
                .build();
        return sendAndReceive(request);
    }

    public CompletableFuture<UserResponse> updateUser(Long userId, Map<String, Object> updates) throws JsonProcessingException {
        String requestId = UUID.randomUUID().toString();
        AuthRequest request = AuthRequest.builder()
                .requestId(requestId)
                .userId(userId.toString())
                .updates(updates)
                .action(AuthRequest.Action.UPDATE_USER)
                .build();
        return sendAndReceive(request);
    }

    private CompletableFuture<UserResponse> sendAndReceive(AuthRequest request) throws JsonProcessingException {
        CompletableFuture<UserResponse> future = new CompletableFuture<>();
        pendingRequests.put(request.getRequestId(), future);

        timeoutScheduler.schedule(() -> {
            CompletableFuture<UserResponse> pending = pendingRequests.remove(request.getRequestId());
            if (pending != null && !pending.isDone()) {
                LOGGER.warn("Request timeout: {}", request.getRequestId());
                UserResponse timeoutResponse = UserResponse.builder()
                        .requestId(request.getRequestId())
                        .errorMessage(UserResponse.ErrorMessage.TIMEOUT)
                        .build();
                pending.complete(timeoutResponse);
            }
        }, 5, TimeUnit.SECONDS);

        kafkaTemplate.send("users-request", request.getRequestId(), objectMapper.writeValueAsString(request))
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