package by.parakhnevich.user.service.impl;

import by.parakhnevich.user.domain.dto.request.LoginRequest;
import by.parakhnevich.user.domain.dto.request.SignUpRequest;
import by.parakhnevich.user.domain.dto.response.UserResponse;
import by.parakhnevich.user.domain.entity.User;
import by.parakhnevich.user.mapper.UserMapper;
import by.parakhnevich.user.repository.UserRepository;
import by.parakhnevich.user.service.AuthService;
import by.parakhnevich.user.service.UserService;
import by.parakhnevich.user.service.exception.IdNotFoundException;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserResponse getUserByUsername(String username) {
        return userMapper.toUserResponse(loadUserByUsername(username));
    }

    @Override
    public UserResponse getUserById(UUID id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                    .orElseThrow(() -> IdNotFoundException.fromId(id.toString())));
    }

    @NullMarked
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                    .orElseThrow(() -> UsernameNotFoundException.fromUsername(username));
    }
}
