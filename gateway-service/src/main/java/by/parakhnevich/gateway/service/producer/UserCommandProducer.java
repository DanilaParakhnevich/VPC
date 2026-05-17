package by.parakhnevich.gateway.service.producer;

import by.parakhnevich.dto.response.UserResponse;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserCommandProducer {
    CompletableFuture<UserResponse> validateToken(String token);

    CompletableFuture<UserResponse> authenticate(String username, String password);

    CompletableFuture<UserResponse> getUserById(UUID id);

    CompletableFuture<UserResponse> getUserByUsername(String username);

    CompletableFuture<UserResponse> updateUser(UUID userId, Map<String, Object> updates);
}
