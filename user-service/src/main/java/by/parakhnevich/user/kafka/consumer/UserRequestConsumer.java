package by.parakhnevich.user.kafka.consumer;

import by.parakhnevich.common.dto.request.user.UserRequest;
import by.parakhnevich.common.dto.response.user.UserResponse;
import by.parakhnevich.user.domain.entity.Role;
import by.parakhnevich.user.kafka.exception.BadCredentialsException;
import by.parakhnevich.user.kafka.exception.UserAlreadyExistsException;
import by.parakhnevich.user.kafka.exception.UserNotFoundException;
import by.parakhnevich.user.repository.UserRepository;
import by.parakhnevich.user.utils.CustomObjectMapper;
import by.parakhnevich.user.utils.JwtService;
import by.parakhnevich.user.utils.PasswordHasher;
import by.parakhnevich.user.utils.mapper.UserMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by agallochum on 2026-05-18
 */
@ApplicationScoped
@RequiredArgsConstructor
public class UserRequestConsumer {

    private static final Logger LOGGER = LogManager.getLogger(UserRequestConsumer.class);

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
    public void consume(String userRequestStr) {
        UserRequest userRequest = null;
        try {
            userRequest = objectMapper.readValue(userRequestStr, UserRequest.class);

            UserResponse response = switch (userRequest) {
                case UserRequest.Register r      -> register(r);
                case UserRequest.Authenticate a  -> authenticate(a);
                case UserRequest.GetById g       -> getById(g);
                case UserRequest.GetByUsername g -> getByUsername(g);
                case UserRequest.GetAll f    -> getAllPageable(f);
                case UserRequest.Update u        -> update(u);
            };

            LOGGER.info("Sending response for request: {}", userRequest.requestId());
            emitter.send(objectMapper.writeValueAsString(response));
        } catch (UserAlreadyExistsException e) {
            LOGGER.error(e.getMessage(), e);
            sendError(UserResponse.ResponseCode.ALREADY_EXISTS, userRequest);
        } catch (BadCredentialsException e) {
            LOGGER.error(e.getMessage(), e);
            sendError(UserResponse.ResponseCode.BAD_PASSWORD, userRequest);
        } catch (UserNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            sendError(UserResponse.ResponseCode.NOT_FOUND, userRequest);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to parse user request", e);
            sendError(UserResponse.ResponseCode.BAD_REQUEST, userRequest);
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            sendError(UserResponse.ResponseCode.BAD_REQUEST, userRequest);
        }
    }

    // ---------- handlers ----------

    public UserResponse.Single register(UserRequest.Register req) {
        if (userRepository.findByUsername(req.username()).isPresent()
                || userRepository.findByEmail(req.email()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        var user = userMapper.toUser(req);
        user.setPassword(passwordHasher.hash(req.password()));
        userRepository.persist(user);

        return authenticate(new UserRequest.Authenticate(
                req.requestId(), req.username(), req.password(), ZonedDateTime.now()));
    }

    public UserResponse.Single authenticate(UserRequest.Authenticate req) {
        var user = userRepository.findByUsername(req.username())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordHasher.matches(req.password(), user.getPassword())) {
            throw new BadCredentialsException();
        }

        var token = jwtService.generateToken(req.username());
        var now = ZonedDateTime.now();
        userRepository.updateLastLoginAt(user.getUsername(), now);

        return userMapper.toSingle(req.requestId(), user)
                .withAccessToken(token)
                .withLastLoginAt(now);
    }

    public UserResponse.Single getById(UserRequest.GetById req) {
        return userRepository.findByIdOptional(Long.parseLong(req.userId()))
                .map(u -> userMapper.toSingle(req.requestId(), u))
                .orElseThrow(UserNotFoundException::new);
    }

    public UserResponse.Single getByUsername(UserRequest.GetByUsername req) {
        return userRepository.findByUsername(req.username())
                .map(u -> userMapper.toSingle(req.requestId(), u))
                .orElseThrow(UserNotFoundException::new);
    }

    public UserResponse.Page getAllPageable(UserRequest.GetAll req) {
        var jpql = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (req.emailLike() != null && !req.emailLike().isBlank()) {
            jpql.append(" and lower(email) like :emailLike");
            params.put("emailLike", "%" + req.emailLike().toLowerCase() + "%");
        }
        if (req.usernameLike() != null && !req.usernameLike().isBlank()) {
            jpql.append(" and lower(username) like :usernameLike");
            params.put("usernameLike", "%" + req.usernameLike().toLowerCase() + "%");
        }
        if (req.createdAfter() != null) {
            jpql.append(" and createdAt >= :createdAfter");
            params.put("createdAfter", req.createdAfter());
        }
        if (req.createdBefore() != null) {
            jpql.append(" and createdAt <= :createdBefore");
            params.put("createdBefore", req.createdBefore());
        }

        var query = userRepository.find(jpql.toString(), params);
        query.page(io.quarkus.panache.common.Page.of(req.page(), req.size()));

        var content = query.list().stream()
                .map(u -> userMapper.toSingle(req.requestId(), u))
                .toList();

        return UserResponse.Page.builder()
                .requestId(req.requestId())
                .content(content)
                .page(req.page())
                .size(req.size())
                .totalElements(query.count())
                .totalPages(query.pageCount())
                .build();
    }

    public UserResponse.Single update(UserRequest.Update req) {
        var user = userRepository.findByIdOptional(Long.parseLong(req.userId()))
                .orElseThrow(UserNotFoundException::new);

        var updates = req.updates();
        if (updates == null || updates.isEmpty()) {
            return userMapper.toSingle(req.requestId(), user);
        }

        if (updates.containsKey("username")) {
            var newUsername = String.valueOf(updates.remove("username"));
            if (newUsername.isBlank()) {
                throw new BadCredentialsException();
            }
            if (!newUsername.equals(user.getUsername())
                    && userRepository.findByUsername(newUsername).isPresent()) {
                throw new UserAlreadyExistsException();
            }
            user.setUsername(newUsername);
        }

        if (updates.containsKey("email")) {
            var newEmail = String.valueOf(updates.remove("email"));
            if (newEmail.isBlank()) {
                throw new BadCredentialsException();
            }
            if (!newEmail.equals(user.getEmail())
                    && userRepository.findByEmail(newEmail).isPresent()) {
                throw new UserAlreadyExistsException();
            }
            user.setEmail(newEmail);
        }

        if (updates.containsKey("password")) {
            var rawPassword = String.valueOf(updates.remove("password"));
            if (rawPassword.isBlank()) {
                throw new BadCredentialsException();
            }
            user.setPassword(passwordHasher.hash(rawPassword));
        }

        if (updates.containsKey("avatarUrl")) {
            user.setAvatarUrl((String) updates.remove("avatarUrl"));
        }

        if (updates.containsKey("role")) {
            var role = String.valueOf(updates.remove("role"));
            if (role.isBlank()) {
                throw new BadCredentialsException();
            }
            user.setRole(Role.valueOf(role));
        }

        if (!updates.isEmpty()) {
            throw new BadRequestException();
        }

        userRepository.getEntityManager().flush();

        return userMapper.toSingle(req.requestId(), user);
    }

    // ---------- helpers ----------

    private void sendError(UserResponse.ResponseCode responseCode, UserRequest request) {
        if (request == null) {
            LOGGER.warn("Cannot send error {} - request not parsed", responseCode);
            return;
        }
        try {
            emitter.send(objectMapper.writeValueAsString(
                    UserResponse.ErrorResponse.builder()
                            .requestId(request.requestId())
                            .responseCode(responseCode)
                            .build()));
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize error response", e);
        }
    }
}