package by.parakhnevich.gateway.controller;

import by.parakhnevich.common.dto.request.user.UserRequest;
import by.parakhnevich.common.dto.response.user.UserResponse;
import by.parakhnevich.gateway.kafka.producer.UserRequestProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Created by agallochum on 2026-05-12
 */
@Tag(name = "Users", description = "User management operations")
@RequestMapping("/api/users")
@RestController
@AllArgsConstructor
public class UserController {

    private static final Logger LOGGER = LogManager.getLogger(UserController.class);
    private static final long HTTP_TIMEOUT_SECONDS = 6;
    private static final Set<String> ALLOWED_UPDATE_FIELDS =
            Set.of("username", "email", "password", "avatarUrl", "role");

    private final UserRequestProducer userRequestProducer;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "Find user by ID or username",
            description = "Returns a single user. Provide **exactly one** parameter: "
                    + "`userId` or `username`. If both or neither are provided — 400."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponse.Single.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "504", description = "user-service did not respond within " + HTTP_TIMEOUT_SECONDS + "s", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error", content = @Content)
    })
    @GetMapping("/find")
    public ResponseEntity<? extends UserResponse> findUser(
            @Parameter(description = "User ID", example = "42")
            @RequestParam(required = false) Long userId,

            @Parameter(description = "Username", example = "user")
            @RequestParam(required = false) String username) {

        if ((userId == null) == (username == null)) {
            LOGGER.warn("findUser called with both or none params: userId={}, username={}", userId, username);
            return ResponseEntity.badRequest().build();
        }

        try {
            CompletableFuture<UserResponse> future = (userId != null)
                    ? userRequestProducer.getUserById(userId)
                    : userRequestProducer.getUserByUsername(username);

            var response = future.get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return toResponse(response);
        } catch (TimeoutException e) {
            LOGGER.error("Timeout for findUser(userId={}, username={})", userId, username, e);
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Get users page (paginated)",
            description = "Returns a page of users with case-insensitive LIKE filters by email and username."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page retrieved",
                    content = @Content(schema = @Schema(implementation = UserResponse.Page.class))),
            @ApiResponse(responseCode = "504", description = "user-service did not respond", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error", content = @Content)
    })
    @GetMapping({"", "/"})
    public ResponseEntity<? extends UserResponse> getAllUsers(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Email filter (partial match)", example = "@gmail.com")
            @RequestParam(required = false) String emailLike,

            @Parameter(description = "Username filter (partial match)", example = "aga")
            @RequestParam(required = false) String usernameLike) {

        try {
            var response = userRequestProducer
                    .getAllUsers(page, size, emailLike, usernameLike)
                    .get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            return toResponse(response);
        } catch (TimeoutException e) {
            LOGGER.error("Timeout for getAllUsers", e);
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Update user",
            description = "Partial update: provided fields are overwritten, "
                    + "null / missing ones stay unchanged. "
                    + "Unknown fields ? 400 Bad Request."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated",
                    content = @Content(schema = @Schema(implementation = UserResponse.Single.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data or unknown fields", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email or username already taken", content = @Content),
            @ApiResponse(responseCode = "504", description = "user-service did not respond", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error", content = @Content)
    })
    @PutMapping("/{userId}")
    public ResponseEntity<? extends UserResponse> updateUser(
            @Parameter(description = "User ID", example = "42")
            @PathVariable Long userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Fields to update",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRequest.Update.class)))
            @RequestBody UserRequest.Update updateRequest) {

        try {
            Map<String, Object> updates = updateRequest.updates() != null
                    ? updateRequest.updates()
                    : Map.of();

            Set<String> unknownFields = updates.keySet().stream()
                    .filter(k -> !ALLOWED_UPDATE_FIELDS.contains(k))
                    .collect(Collectors.toSet());

            if (!unknownFields.isEmpty()) {
                LOGGER.warn("updateUser({}) — unknown fields: {}", userId, unknownFields);
                return ResponseEntity.badRequest()
                        .body(UserResponse.ErrorResponse.builder()
                                .requestId(updateRequest.requestId())
                                .responseCode(UserResponse.ResponseCode.BAD_REQUEST)
                                .message("Unknown fields: " + unknownFields)
                                .build());
            }

            if (updates.isEmpty()) {
                var current = userRequestProducer.getUserById(userId)
                        .get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                return toResponse(current);
            }

            var response = userRequestProducer
                    .updateUser(userId, updates)
                    .get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            return toResponse(response);
        } catch (TimeoutException e) {
            LOGGER.error("Timeout for updateUser({})", userId, e);
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<? extends UserResponse> toResponse(UserResponse response) {
        return switch (response) {
            case UserResponse.Single s -> ResponseEntity.status(s.responseCode().getCode()).body(s);
            case UserResponse.Page p -> ResponseEntity.status(p.responseCode().getCode()).body(p);
            case UserResponse.ErrorResponse e -> ResponseEntity.status(e.responseCode().getCode()).body(e);
        };
    }
}