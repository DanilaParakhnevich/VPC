package by.parakhnevich.user.kafka.consumer;

import by.parakhnevich.dto.request.user.AuthRequest;
import by.parakhnevich.dto.response.user.UserResponse;
import by.parakhnevich.user.domain.entity.User;
import by.parakhnevich.user.repository.UserRepository;
import by.parakhnevich.user.utils.CustomObjectMapper;
import by.parakhnevich.user.utils.JwtService;
import by.parakhnevich.user.utils.PasswordHasher;
import by.parakhnevich.user.utils.mapper.UserMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.awaitility.Awaitility;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by agallochum on 2026-09-03
 */
@QuarkusTest
class UserRequestConsumerTest {

    @InjectMock
    UserRepository userRepository;

    @InjectMock
    JwtService jwtService;

    @InjectMock
    PasswordHasher passwordHasher;

    @InjectMock
    UserMapper userMapper;

    @Inject
    CustomObjectMapper objectMapper;

    @Inject
    @Any
    InMemoryConnector connector;

    private InMemorySource<String> source;
    private InMemorySink<String> sink;

    @BeforeEach
    void setUp() {
        source = connector.source("users-request");
        sink = connector.sink("users-response");

        sink.clear();
    }

    @AfterEach
    void tearDown() {

        sink.clear();
    }

    @Test
    void shouldRegisterNewUserAndReturnToken() throws Exception {
        String requestId = "req-001";

        AuthRequest request = new AuthRequest();
        request.setAction(AuthRequest.Action.REGISTER);
        request.setUsername("john_doe");
        request.setPassword("secret");
        request.setEmail("john@example.com");
        request.setRequestId(requestId);

        when(passwordHasher.hash("secret")).thenReturn("hashed_secret");

        User userEntity = new User();
        userEntity.setUsername("john_doe");
        userEntity.setEmail("john@example.com");
        userEntity.setPassword("hashed_secret");
        when(userMapper.toUser(any(AuthRequest.class))).thenReturn(userEntity);

        when(userRepository.findByUsername("john_doe"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(userEntity));

        when(passwordHasher.matches("secret", "hashed_secret")).thenReturn(true);
        when(jwtService.generateToken("john_doe")).thenReturn("jwt-token-123");

        UserResponse responseMock = new UserResponse();
        responseMock.setUsername("john_doe");
        responseMock.setEmail("john@example.com");
        responseMock.setAccessToken("jwt-token-123");
        when(userMapper.toUserResponse(any(User.class))).thenReturn(responseMock);

        String jsonRequest = objectMapper.writeValueAsString(request);
        source.send(jsonRequest);

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .until(() -> sink.received().stream()
                        .anyMatch(msg -> {
                            try {
                                UserResponse resp = objectMapper.readValue(msg.getPayload(), UserResponse.class);
                                return requestId.equals(resp.getRequestId());
                            } catch (Exception e) {
                                return false;
                            }
                        }));

        Message<String> received = sink.received().stream()
                .filter(msg -> {
                    try {
                        UserResponse resp = objectMapper.readValue(msg.getPayload(), UserResponse.class);
                        return requestId.equals(resp.getRequestId());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .findFirst()
                .orElseThrow();

        UserResponse actualResponse = objectMapper.readValue(received.getPayload(), UserResponse.class);

        assertThat(actualResponse.getUsername()).isEqualTo("john_doe");
        assertThat(actualResponse.getEmail()).isEqualTo("john@example.com");
        assertThat(actualResponse.getRequestId()).isEqualTo("req-001");
        assertThat(actualResponse.getAccessToken()).isEqualTo("jwt-token-123");
        assertThat(actualResponse.getErrorMessage()).isEqualTo(UserResponse.ErrorMessage.NONE);
    }
}