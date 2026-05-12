package by.parakhnevich.user.controller;

import by.parakhnevich.user.domain.dto.response.UserResponse;
import by.parakhnevich.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("api/users")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/username/{username}")
    public UserResponse getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/{id}")
    public UserResponse getUserByPassword(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

}
