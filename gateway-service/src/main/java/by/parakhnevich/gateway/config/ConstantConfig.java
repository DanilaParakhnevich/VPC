package by.parakhnevich.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ConstantConfig {
    @Bean
    public List<String> publicUris() {
        return List.of(
                "/error",
                "/api/auth/login",
                "/api/auth/register",
                "/api/users",
                "/public",
                "/actuator/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**"
        );
    }
}
