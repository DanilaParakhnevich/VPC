package by.parakhnevich.gateway.kafka.producer;

import by.parakhnevich.common.dto.request.user.UserRequest;
import by.parakhnevich.common.dto.response.user.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by agallochum on 2026-05-15
 */
@Service
@Getter
public class UserRequestProducer {

    private static final Logger LOGGER = LogManager.getLogger(UserRequestProducer.class);
    private static final String TOPIC = "users-request";
    private static final long TIMEOUT_SECONDS = 5;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, CompletableFuture<UserResponse>> pendingRequests = new ConcurrentHashMap<>();
    private final ScheduledExecutorService timeoutScheduler = Executors.newScheduledThreadPool(10);

    public CompletableFuture<UserResponse> register(String email, String username, String password)
            throws JsonProcessingException {
        return sendAndReceive(UserRequest.Register.builder()
                .email(email)
                .username(username)
                .password(password)
                .build());
    }

    public CompletableFuture<UserResponse> authenticate(String username, String password)
            throws JsonProcessingException {
        return sendAndReceive(UserRequest.Authenticate.builder()
                .username(username)
                .password(password)
                .build());
    }

    public CompletableFuture<UserResponse> getUserById(Long id) throws JsonProcessingException {
        return sendAndReceive(UserRequest.GetById.builder()
                .userId(String.valueOf(id))
                .build());
    }

    public CompletableFuture<UserResponse> getUserByUsername(String username)
            throws JsonProcessingException {
        return sendAndReceive(UserRequest.GetByUsername.builder()
                .username(username)
                .build());
    }

    public CompletableFuture<UserResponse> getAllUsers(int page, int size,
                                                       String emailLike, String usernameLike)
            throws JsonProcessingException {
        return sendAndReceive(UserRequest.GetAll.builder()
                .page(page)
                .size(size)
                .emailLike(emailLike)
                .usernameLike(usernameLike)
                .build());
    }

    public CompletableFuture<UserResponse> updateUser(Long userId, Map<String, Object> updates)
            throws JsonProcessingException {
        return sendAndReceive(UserRequest.Update.builder()
                .userId(String.valueOf(userId))
                .updates(updates)
                .build());
    }

    private CompletableFuture<UserResponse> sendAndReceive(UserRequest request)
            throws JsonProcessingException {

        var future = new CompletableFuture<UserResponse>();
        pendingRequests.put(request.requestId(), future);

        timeoutScheduler.schedule(() -> {
            var pending = pendingRequests.remove(request.requestId());
            if (pending != null && !pending.isDone()) {
                LOGGER.warn("Request timeout: {}", request.requestId());
                pending.complete(UserResponse.ErrorResponse.builder()
                        .requestId(request.requestId())
                        .responseCode(UserResponse.ResponseCode.TIMEOUT)
                        .message("user-service did not respond")
                        .build());
            }
        }, TIMEOUT_SECONDS, TimeUnit.SECONDS);

        kafkaTemplate.send(TOPIC, request.requestId(), objectMapper.writeValueAsString(request))
                .whenComplete((result, e) -> {
                    if (e != null) {
                        LOGGER.error("Failed to send Kafka message: {}", request.requestId(), e);
                        var pending = pendingRequests.remove(request.requestId());
                        if (pending != null && !pending.isDone()) {
                            pending.completeExceptionally(e);
                        }
                    } else {
                        LOGGER.debug("Sent Kafka message: {} to partition {}",
                                request.requestId(),
                                result.getRecordMetadata().partition());
                    }
                });

        return future;
    }
}