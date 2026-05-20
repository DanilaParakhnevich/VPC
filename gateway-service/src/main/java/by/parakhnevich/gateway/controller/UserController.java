package by.parakhnevich.gateway.controller;

import by.parakhnevich.dto.response.UserResponse;
import by.parakhnevich.gateway.kafka.producer.UserRequestProducer;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequestMapping("/api/users")
@RestController
@AllArgsConstructor
public class UserController {

    private static final Logger LOGGER = LogManager.getLogger(UserController.class);
    private UserRequestProducer userRequestProducer;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable UUID userId) {

        CompletableFuture<UserResponse> response = userRequestProducer.getUserById(userId);

        try {
            UserResponse userResponse = response.get();
            if (UserResponse.ErrorMessage.NOT_FOUND.equals(userResponse.getErrorMessage())) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(userResponse);
            }
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
