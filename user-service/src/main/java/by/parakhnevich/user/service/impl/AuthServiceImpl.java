package by.parakhnevich.user.service.impl;

import by.parakhnevich.user.domain.dto.request.LoginRequest;
import by.parakhnevich.user.domain.dto.request.SignUpRequest;
import by.parakhnevich.user.domain.dto.response.AuthResponse;
import by.parakhnevich.user.domain.dto.response.SignUpResponse;
import by.parakhnevich.user.mapper.UserMapper;
import by.parakhnevich.user.repository.UserRepository;
import by.parakhnevich.user.service.AuthService;
import by.parakhnevich.user.service.JwtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by agallochum on 2026-05-12
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LogManager.getLogger(AuthServiceImpl.class);

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
        Instant lastLoginInstant = new Date().toInstant();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        String token = jwtService.generateToken(authentication.getName());

        var authResponse = userMapper.toAuthResponse(userMapper.toUser(loginRequest));

        authResponse.setAccessToken(token);
        authResponse.setExpiresIn(TimeUnit.MILLISECONDS.toChronoUnit()
                .between(jwtService.extractExpiration(token).toInstant(), lastLoginInstant));
        authResponse.setTokenType("Bearer");
        authResponse.setLastLoginAt(ZonedDateTime.from(lastLoginInstant));

        userRepository.updateLastLoginAt(loginRequest.getUsername(), authResponse.getLastLoginAt());

        LOGGER.info("Login successfully by {}", loginRequest.getUsername());

        return authResponse;
    }

    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        var user = userMapper.toUser(signUpRequest);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        SignUpResponse signUpResponse = userMapper.toSignUpResponse(userRepository.save(user));

        LOGGER.info("Sign up successfully by {}", signUpRequest.getUsername());

        // LOGIN IN AFTER SIGN UP

        AuthResponse loginResponse = login(new LoginRequest(signUpRequest.getUsername(), signUpRequest.getPassword()));

        signUpResponse.setAccessToken(loginResponse.getAccessToken());
        signUpResponse.setLastLoginAt(loginResponse.getLastLoginAt());
        signUpResponse.setExpiresIn(loginResponse.getExpiresIn());
        signUpResponse.setTokenType(loginResponse.getTokenType());

        return signUpResponse;
    }
}
