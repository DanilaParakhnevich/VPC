package by.parakhnevich.gateway.controller;

import by.parakhnevich.dto.response.user.UserResponse;
import by.parakhnevich.gateway.kafka.producer.UserRequestProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * Created by agallochum on 2026-05-12
 */
@RequestMapping("/api/users")
@RestController
@AllArgsConstructor
public class UserController {

    private static final Logger LOGGER = LogManager.getLogger(UserController.class);
    private UserRequestProducer userRequestProducer;

    @Operation(summary = "Get user by id", description = "Returns user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "User not found or bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        try {
            CompletableFuture<UserResponse> response = userRequestProducer.getUserById(userId);

            UserResponse userResponse = response.get();
            return ResponseEntity.status(userResponse.getErrorMessage().getCode()).body(userResponse);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

//    @GetMapping("/{type}/{field}")
//    public ResponseEntity<?> getUser(@PathVariable String type, @PathVariable String field) {
//        try {
//            switch (type) {
//                case "id":
//                    return ResponseEntity.ok(userService.getUserById(UUID.fromString(field)));
//                case "username":
//                    return ResponseEntity.ok(userService.getUserByUsername(field));
//                default:
//                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
//            }
//        } catch (UsernameNotFoundException | IdNotFoundException e) {
//            LOGGER.error(e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}
