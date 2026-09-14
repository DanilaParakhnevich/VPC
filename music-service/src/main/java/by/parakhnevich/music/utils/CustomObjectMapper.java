package by.parakhnevich.music.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Created by agallochum on 2026-05-18
 */
@ApplicationScoped
public class CustomObjectMapper extends ObjectMapper {
    public CustomObjectMapper() {
        super();
        registerModule(new JavaTimeModule());
    }
}
