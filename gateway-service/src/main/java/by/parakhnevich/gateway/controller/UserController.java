package by.parakhnevich.gateway.controller;

import by.parakhnevich.common.dto.response.user.UserResponse;
import by.parakhnevich.gateway.kafka.producer.UserRequestProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by agallochum on 2026-05-12
 */
@RequestMapping("/api/users")
@RestController
@AllArgsConstructor
public class UserController {

    private static final Logger LOGGER = LogManager.getLogger(UserController.class);
    private static final long HTTP_TIMEOUT_SECONDS = 6;

    private final UserRequestProducer userRequestProducer;

    @Operation(summary = "Get user by id", description = "Returns user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<? extends UserResponse> getUser(@PathVariable Long userId) {
        try {
            var response = userRequestProducer.getUserById(userId)
                    .get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            return switch (response) {
                case UserResponse.Single s        -> ResponseEntity.status(s.errorMessage().getCode()).body(s);
                case UserResponse.ErrorResponse e -> ResponseEntity.status(e.errorMessage().getCode()).body(e);
                default -> ResponseEntity.internalServerError().build();
            };
        } catch (TimeoutException e) {
            LOGGER.error("Timeout for getUser({})", userId, e);
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get all users pageable", description = "Returns users pageable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping
    public ResponseEntity<? extends UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false)    String emailLike,
            @RequestParam(required = false)    String usernameLike) {

        try {
            var response = userRequestProducer
                    .getAllUsers(page, size, emailLike, usernameLike)
                    .get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            return switch (response) {
                case UserResponse.Page p          -> ResponseEntity.status(p.errorMessage().getCode()).body(p);
                case UserResponse.ErrorResponse e -> ResponseEntity.status(e.errorMessage().getCode()).body(e);
                default -> ResponseEntity.internalServerError().build();
            };
        } catch (TimeoutException e) {
            LOGGER.error("Timeout for getAllUsers", e);
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}