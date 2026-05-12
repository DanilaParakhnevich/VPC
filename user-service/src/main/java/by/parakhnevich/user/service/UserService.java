package by.parakhnevich.user.service;

import by.parakhnevich.user.domain.dto.response.UserResponse;
import by.parakhnevich.user.domain.entity.User;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

public interface UserService extends UserDetailsService {

    UserResponse getUserByUsername(String username);

    UserResponse getUserById(UUID id);

    @NullMarked
    User loadUserByUsername(String username) throws UsernameNotFoundException;
}
