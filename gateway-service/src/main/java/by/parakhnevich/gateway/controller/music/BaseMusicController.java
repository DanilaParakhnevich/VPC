package by.parakhnevich.gateway.controller.music;

import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.gateway.kafka.producer.MusicRequestProducer;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by agallochum on 2026-09-25
 */
@RequiredArgsConstructor
public abstract class BaseMusicController {

    private static final Logger LOGGER = LogManager.getLogger(BaseMusicController.class);

    protected final MusicRequestProducer producer;

    protected ResponseEntity<? extends MusicResponse> toResponse(MusicResponse response) {
        return switch (response) {
            case MusicResponse.Deleted d       -> ResponseEntity.status(d.responseCode().getCode()).body(d);
            case MusicResponse.Single s        -> ResponseEntity.status(s.responseCode().getCode()).body(s);
            case MusicResponse.Page p          -> ResponseEntity.status(p.responseCode().getCode()).body(p);
            case MusicResponse.ErrorResponse e -> ResponseEntity.status(e.responseCode().getCode()).body(e);
        };
    }

    protected ResponseEntity<? extends MusicResponse> timeout(String op, Exception e) {
        LOGGER.error("Timeout for {}", op, e);
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
    }

    protected ResponseEntity<? extends MusicResponse> internal(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return ResponseEntity.internalServerError().build();
    }
}