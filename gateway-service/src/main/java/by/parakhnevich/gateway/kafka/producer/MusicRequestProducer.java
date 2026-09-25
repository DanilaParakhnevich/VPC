package by.parakhnevich.gateway.kafka.producer;

import by.parakhnevich.common.dto.request.music.MusicRequest;
import by.parakhnevich.common.dto.response.music.MusicResponse;
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
 * Created by agallochum on 2026-09-25
 */
@Service
@Getter
public class MusicRequestProducer {

    private static final Logger LOGGER = LogManager.getLogger(MusicRequestProducer.class);
    private static final String TOPIC = "music-request";
    private static final long TIMEOUT_SECONDS = 5;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, CompletableFuture<MusicResponse>> pendingRequests = new ConcurrentHashMap<>();
    private final ScheduledExecutorService timeoutScheduler = Executors.newScheduledThreadPool(10);

    public CompletableFuture<MusicResponse> sendAndReceive(MusicRequest request)
            throws JsonProcessingException {

        var future = new CompletableFuture<MusicResponse>();
        pendingRequests.put(request.requestId(), future);

        timeoutScheduler.schedule(() -> {
            var pending = pendingRequests.remove(request.requestId());
            if (pending != null && !pending.isDone()) {
                LOGGER.warn("Request timeout: {}", request.requestId());
                pending.complete(MusicResponse.ErrorResponse.builder()
                        .requestId(request.requestId())
                        .responseCode(MusicResponse.ResponseCode.TIMEOUT)
                        .message("music-service did not respond")
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