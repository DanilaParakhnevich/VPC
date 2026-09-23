package by.parakhnevich.music.kafka.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by agallochum on 2026-05-18
 */
@ApplicationScoped
@RequiredArgsConstructor
public class MediaRequestConsumer {

    private static final Logger LOGGER = LogManager.getLogger(MediaRequestConsumer.class);

}