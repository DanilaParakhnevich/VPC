package by.parakhnevich.user.service;

import by.parakhnevich.user.domain.dto.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserService extends UserDetailsService {
    UserResponse getUserByUsername(String username);
    UserResponse getUserById(UUID id);
}
