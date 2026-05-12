package by.parakhnevich.user.controller;

import by.parakhnevich.user.domain.dto.response.UserResponse;
import by.parakhnevich.user.service.UserService;
import by.parakhnevich.user.service.exception.IdNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/users")
@RestController
public class UserController {

    private static final Logger LOGGER = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/{type}/{field}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String type, @PathVariable String field) {
        try {
            switch (type) {
                case "id":
                    return ResponseEntity.ok(userService.getUserById(UUID.fromString(field)));
                case "username":
                    return ResponseEntity.ok(userService.getUserByUsername(field));
                default:
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
            }
        } catch (UsernameNotFoundException | IdNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
