package by.parakhnevich.user.service.impl;

import by.parakhnevich.user.domain.dto.request.LoginRequest;
import by.parakhnevich.user.domain.dto.request.SignUpRequest;
import by.parakhnevich.user.domain.dto.response.AuthResponse;
import by.parakhnevich.user.domain.dto.response.SignUpResponse;
import by.parakhnevich.user.domain.dto.response.UserResponse;
import by.parakhnevich.user.mapper.UserMapper;
import by.parakhnevich.user.repository.UserRepository;
import by.parakhnevich.user.service.AuthService;
import by.parakhnevich.user.service.JwtService;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by agallochum on 2026-05-12
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;


    @Override
    public AuthResponse login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        String token = jwtService.generateToken(authentication.getName());

        var authResponse = userMapper.toAuthResponse(userMapper.toUser(loginRequest));

        authResponse.setAccessToken(token);
        authResponse.setExpiresIn(TimeUnit.MILLISECONDS.toChronoUnit()
                .between(jwtService.extractExpiration(token).toInstant(), new Date().toInstant()));
        authResponse.setTokenType("Bearer");

        return authResponse;
    }

    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        var user = userMapper.toUser(signUpRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.toSignUpResponse(userRepository.save(user));
    }
}
