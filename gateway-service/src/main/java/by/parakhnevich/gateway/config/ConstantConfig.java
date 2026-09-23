package by.parakhnevich.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by agallochum on 2026-09-02
 */
@Configuration
public class ConstantConfig {
    @Bean
    public List<String> publicUris() {
        return List.of(
                "/error",
                "/api/auth/login",
                "/api/auth/register",
                "/public",
                "/actuator/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**"
        );
    }
}
