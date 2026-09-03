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
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

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
        AuthRequest request = new AuthRequest();
        request.setAction(AuthRequest.Action.REGISTER);
        request.setUsername("john_doe");
        request.setPassword("secret");
        request.setEmail("john@example.com");
        request.setRequestId("req-001");

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        when(passwordHasher.hash("secret")).thenReturn("hashed_secret");

        User userEntity = new User();
        userEntity.setUsername("john_doe");
        userEntity.setEmail("john@example.com");
        userEntity.setPassword("hashed_secret");
        when(userMapper.toUser(any(AuthRequest.class))).thenReturn(userEntity);

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(userEntity));

        when(passwordHasher.matches("secret", "hashed_secret")).thenReturn(true);
        when(jwtService.generateToken("john_doe")).thenReturn("jwt-token-123");

        UserResponse responseMock = new UserResponse();
        responseMock.setUsername("john_doe");
        responseMock.setEmail("john@example.com");
        responseMock.setAccessToken("jwt-token-123");
        when(userMapper.toUserResponse(any(User.class))).thenReturn(responseMock);

        String jsonRequest = objectMapper.writeValueAsString(request);
        source.send(jsonRequest);

        Message<String> received = sink.received().getFirst();
        assertThat(received).isNotNull();

        UserResponse actualResponse = objectMapper.readValue(received.getPayload(), UserResponse.class);

        assertThat(actualResponse.getRequestId()).isEqualTo("req-001");
        assertThat(actualResponse.getAccessToken()).isEqualTo("jwt-token-123");
        assertThat(actualResponse.getErrorMessage()).isNull();
    }

    @Test
    void shouldAuthenticateExistingUser() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setAction(AuthRequest.Action.AUTHENTICATE);
        request.setUsername("john_doe");
        request.setPassword("secret");
        request.setRequestId("req-002");

        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("hashed_secret");

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("secret", "hashed_secret")).thenReturn(true);
        when(jwtService.generateToken("john_doe")).thenReturn("jwt-token-456");

        UserResponse responseMock = new UserResponse();
        responseMock.setUsername("john_doe");
        responseMock.setAccessToken("jwt-token-456");
        when(userMapper.toUserResponse(any(User.class))).thenReturn(responseMock);

        source.send(objectMapper.writeValueAsString(request));
        Message<String> received = sink.received().getFirst();
        UserResponse actual = objectMapper.readValue(received.getPayload(), UserResponse.class);

        assertThat(actual.getRequestId()).isEqualTo("req-002");
        assertThat(actual.getAccessToken()).isEqualTo("jwt-token-456");
        assertThat(actual.getErrorMessage()).isNull();
    }

    @Test
    void shouldReturnBadPasswordOnInvalidCredentials() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setAction(AuthRequest.Action.AUTHENTICATE);
        request.setUsername("john_doe");
        request.setPassword("wrong");
        request.setRequestId("req-003");

        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("hashed_secret");

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("wrong", "hashed_secret")).thenReturn(false);

        source.send(objectMapper.writeValueAsString(request));
        Message<String> received = sink.received().getFirst();
        UserResponse actual = objectMapper.readValue(received.getPayload(), UserResponse.class);

        assertThat(actual.getRequestId()).isEqualTo("req-003");
        assertThat(actual.getErrorMessage()).isEqualTo(UserResponse.ErrorMessage.BAD_PASSWORD);
        assertThat(actual.getAccessToken()).isNull();
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setAction(AuthRequest.Action.AUTHENTICATE);
        request.setUsername("unknown");
        request.setPassword("any");
        request.setRequestId("req-004");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        source.send(objectMapper.writeValueAsString(request));
        Message<String> received = sink.received().getFirst();
        UserResponse actual = objectMapper.readValue(received.getPayload(), UserResponse.class);

        assertThat(actual.getRequestId()).isEqualTo("req-004");
        assertThat(actual.getErrorMessage()).isEqualTo(UserResponse.ErrorMessage.NOT_FOUND);
    }

    @Test
    void shouldValidateTokenSuccessfully() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setAction(AuthRequest.Action.VALIDATE);
        request.setToken("valid-jwt");
        request.setRequestId("req-005");

        User user = new User();
        user.setUsername("john_doe");

        when(jwtService.extractUsername("valid-jwt")).thenReturn("john_doe");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(jwtService.validateToken("valid-jwt", user)).thenReturn(true);

        UserResponse responseMock = new UserResponse();
        responseMock.setUsername("john_doe");
        when(userMapper.toUserResponse(any(User.class))).thenReturn(responseMock);

        source.send(objectMapper.writeValueAsString(request));
        Message<String> received = sink.received().getFirst();
        UserResponse actual = objectMapper.readValue(received.getPayload(), UserResponse.class);

        assertThat(actual.getRequestId()).isEqualTo("req-005");
        assertThat(actual.getErrorMessage()).isNull();
    }

    @Test
    void shouldReturnBadTokenWhenValidationFails() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setAction(AuthRequest.Action.VALIDATE);
        request.setToken("invalid-jwt");
        request.setRequestId("req-006");

        User user = new User();
        user.setUsername("john_doe");

        when(jwtService.extractUsername("invalid-jwt")).thenReturn("john_doe");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(jwtService.validateToken("invalid-jwt", user)).thenReturn(false);

        source.send(objectMapper.writeValueAsString(request));
        Message<String> received = sink.received().getFirst();
        UserResponse actual = objectMapper.readValue(received.getPayload(), UserResponse.class);

        assertThat(actual.getRequestId()).isEqualTo("req-006");
        assertThat(actual.getErrorMessage()).isEqualTo(UserResponse.ErrorMessage.BAD_TOKEN);
    }
}