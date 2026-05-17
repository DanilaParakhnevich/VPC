package by.parakhnevich.user.kafka;

import by.parakhnevich.dto.request.UserRequest;
import by.parakhnevich.dto.response.UserResponse;
import by.parakhnevich.user.domain.entity.Role;
import by.parakhnevich.user.domain.entity.User;
import by.parakhnevich.user.repository.UserRepository;
import by.parakhnevich.user.utils.PasswordHasher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by agallochum on 2026-05-16
 */
@Component
@RequiredArgsConstructor
public class UserRequestListener {

    private static final Logger LOGGER = LogManager.getLogger(UserRequestListener.class);

    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PasswordHasher passwordEncoder;

    @KafkaListener(
            topics = "${kafka.topics.user-request}",
            groupId = "user-service-group",
            concurrency = "3"
    )
    @Transactional
    public void handleRequest(UserRequest request) {
        LOGGER.info("Processing request: {} - {}", request.getRequestId(), request.getAction());

        UserResponse response = switch (request.getAction()) {
            case VALIDATE -> validateToken(request);
            case AUTHENTICATE -> authenticate(request);
            case GET_USER_BY_ID -> getUserById(request);
            case GET_USER_BY_USERNAME -> getUserByUsername(request);
            case UPDATE_USER -> updateUser(request);
        };

        kafkaTemplate.send("user-response", response.getRequestId(), response)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        LOGGER.error("Failed to send response for: {}", request.getRequestId(), ex);
                    } else {
                        LOGGER.info("Sent response for: {}", request.getRequestId());
                    }
                });
    }

    private UserResponse validateToken(UserRequest request) {
        String username = parseUsernameFromToken(request.getToken());
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return UserResponse.builder()
                    .requestId(request.getRequestId())
                    .userId(String.valueOf(user.getId()))
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().toString())
                    .status(UserResponse.Status.OK)
                    .build();
        } else {
            return UserResponse.builder()
                    .requestId(request.getRequestId())
                    .errorMessage(UserResponse.ErrorMessage.NOT_FOUND)
                    .status(UserResponse.Status.NOT_FOUND)
                    .build();
        }
    }

    private UserResponse authenticate(UserRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            return UserResponse.builder()
                    .requestId(request.getRequestId())
                    .errorMessage(UserResponse.ErrorMessage.NOT_FOUND)
                    .status(UserResponse.Status.NOT_FOUND)
                    .build();
        }

        User user = userOpt.get();

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return UserResponse.builder()
                    .requestId(request.getRequestId())
                    .userId(String.valueOf(user.getId()))
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().toString())
                    .build();
        } else {
            return UserResponse.builder()
                    .requestId(request.getRequestId())
                    .errorMessage(UserResponse.ErrorMessage.BAD_PASSWORD)
                    .status(UserResponse.Status.VALIDATION_ERROR)
                    .build();
        }
    }

    private UserResponse getUserById(UserRequest request) {
        Optional<User> userOpt = userRepository.findById(UUID.fromString(request.getUserId()));

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return UserResponse.builder()
                    .requestId(request.getRequestId())
                    .userId(String.valueOf(user.getId()))
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().toString())
                    .status(UserResponse.Status.OK)
                    .build();
        } else {
            return UserResponse.builder()
                    .requestId(request.getRequestId())
                    .errorMessage(UserResponse.ErrorMessage.NOT_FOUND)
                    .status(UserResponse.Status.NOT_FOUND)
                    .build();
        }
    }

    private UserResponse getUserByUsername(UserRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return UserResponse.builder()
                    .requestId(request.getRequestId())
                    .userId(String.valueOf(user.getId()))
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().toString())
                    .status(UserResponse.Status.OK)
                    .build();
        } else {
            return UserResponse.builder()
                    .requestId(request.getRequestId())
                    .errorMessage(UserResponse.ErrorMessage.NOT_FOUND)
                    .status(UserResponse.Status.NOT_FOUND)
                    .build();
        }
    }

    private UserResponse updateUser(UserRequest request) {
        Optional<User> userOpt = userRepository.findById(UUID.fromString(request.getUserId()));

        if (userOpt.isEmpty()) {
            return UserResponse.builder()
                    .requestId(request.getRequestId())
                    .errorMessage(UserResponse.ErrorMessage.NOT_FOUND)
                    .status(UserResponse.Status.NOT_FOUND)
                    .build();
        }

        User user = userOpt.get();
        Map<String, Object> updates = request.getUpdates();

        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }
        if (updates.containsKey("role")) {
            user.setRole(Role.valueOf((String) updates.get("role")));
        }

        userRepository.save(user);

        return UserResponse.builder()
                .requestId(request.getRequestId())
                .userId(String.valueOf(user.getId()))
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .build();
    }

    private String parseUsernameFromToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new RuntimeException("Invalid JWT format");
        }

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(payload);
            return node.get("sub").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT", e);
        }
    }
}