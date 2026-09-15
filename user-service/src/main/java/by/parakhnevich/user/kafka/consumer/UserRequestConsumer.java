package by.parakhnevich.user.kafka.consumer;

import by.parakhnevich.dto.request.user.AuthRequest;
import by.parakhnevich.dto.response.user.UserResponse;
import by.parakhnevich.user.kafka.exception.BadCredentialsException;
import by.parakhnevich.user.kafka.exception.UserAlreadyExistsException;
import by.parakhnevich.user.kafka.exception.UserNotFoundException;
import by.parakhnevich.user.repository.UserRepository;
import by.parakhnevich.user.utils.CustomObjectMapper;
import by.parakhnevich.user.utils.JwtService;
import by.parakhnevich.user.utils.PasswordHasher;
import by.parakhnevich.user.utils.mapper.UserMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.ZonedDateTime;

/**
 * Created by agallochum on 2026-05-18
 */
@ApplicationScoped
@RequiredArgsConstructor
public class UserRequestConsumer {

    private static final Logger LOGGER = LogManager.getLogger(UserRequestConsumer.class.getName());

    @Inject
    @Channel("users-response")
    Emitter<String> emitter;
    @Inject
    UserRepository userRepository;
    @Inject
    CustomObjectMapper objectMapper;
    @Inject
    JwtService jwtService;
    @Inject
    PasswordHasher passwordHasher;
    @Inject
    UserMapper userMapper;

    @Incoming("users-request")
    @Transactional
    public void consume(String userRequestStr) throws JsonProcessingException {
        var userRequest = objectMapper.readValue(userRequestStr, AuthRequest.class);

        try {
            var user = switch (userRequest.getAction()) {
                case REGISTER -> register(userRequest);
                case AUTHENTICATE -> authenticate(userRequest);
                case UPDATE_USER -> update(userRequest);
                case GET_USER_BY_ID -> getById(userRequest);
                case GET_USER_BY_USERNAME -> getByUsername(userRequest);
            };

            user.setRequestId(userRequest.getRequestId());

            LOGGER.info("Sending response for request: {}", userRequest.getRequestId());
            emitter.send(objectMapper.writeValueAsString(user));
        } catch (UserAlreadyExistsException e) {
            LOGGER.error(e.getMessage(), e);
            sendErrorMessageToEmitter(UserResponse.ErrorMessage.ALREADY_EXISTS, userRequest.getRequestId());
        } catch (BadCredentialsException e) {
            LOGGER.error(e.getMessage(), e);
            sendErrorMessageToEmitter(UserResponse.ErrorMessage.BAD_PASSWORD, userRequest.getRequestId());
        }  catch (UserNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            sendErrorMessageToEmitter(UserResponse.ErrorMessage.NOT_FOUND, userRequest.getRequestId());
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to parse user request", e);
            sendErrorMessageToEmitter(UserResponse.ErrorMessage.BAD_REQUEST, userRequest.getRequestId());
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            sendErrorMessageToEmitter(UserResponse.ErrorMessage.BAD_REQUEST, userRequest.getRequestId());
        }

    }

    public UserResponse register(AuthRequest authRequest) {
        try {
            var user = userMapper.toUser(authRequest);
            user.setPassword(passwordHasher.hash(authRequest.getPassword()));
            if (userRepository.findByUsername(user.getUsername()).isPresent()
                    || userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new UserAlreadyExistsException();
            } else {
                userRepository.persist(user);
                return authenticate(authRequest);
            }
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public UserResponse authenticate(AuthRequest authRequest) {
        var user = userRepository.findByUsername(authRequest.getUsername());

        if (user.isPresent()) {
            if (passwordHasher.matches(authRequest.getPassword(), user.get().getPassword())) {
                String token = jwtService.generateToken(authRequest.getUsername());
                UserResponse response = userMapper.toUserResponse(user.get());
                var updateTime = ZonedDateTime.now();
                userRepository.updateLastLoginAt(user.get().getUsername(), updateTime);
                response.setAccessToken(token);
                response.setLastLoginAt(updateTime);
                return response;
            } else {
                throw new BadCredentialsException();
            }
        } else {
            throw new UserNotFoundException();
        }
    }

    public UserResponse update(AuthRequest authRequest) {
        throw new UnsupportedOperationException("Update not implemented yet");
    }

    public UserResponse getById(AuthRequest authRequest) {
        var user = userRepository.findByIdOptional(Long.parseLong(authRequest.getUserId()));
        if (user.isPresent()) {
            return userMapper.toUserResponse(user.get());
        } else {
            throw new UserNotFoundException();
        }
    }

    public UserResponse getByUsername(AuthRequest authRequest) {
        var user = userRepository.findByUsername(authRequest.getUsername());
        if (user.isPresent()) {
            return userMapper.toUserResponse(user.get());
        } else {
            throw new UserNotFoundException();
        }
    }

    private void sendErrorMessageToEmitter(UserResponse.ErrorMessage errorMessage, String requestId) throws JsonProcessingException {
        emitter.send(objectMapper.writeValueAsString(
                UserResponse.builder()
                        .requestId(requestId)
                        .errorMessage(errorMessage)
                        .build()));
    }
}