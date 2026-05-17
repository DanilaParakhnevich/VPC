package by.parakhnevich.gateway.service.impl;

import by.parakhnevich.gateway.domain.dto.request.LoginRequest;
import by.parakhnevich.gateway.domain.dto.request.SignUpRequest;
import by.parakhnevich.gateway.domain.dto.response.AuthResponse;
import by.parakhnevich.gateway.domain.dto.response.SignUpResponse;
import by.parakhnevich.gateway.service.AuthService;
import by.parakhnevich.gateway.service.JwtService;
import by.parakhnevich.gateway.util.mapper.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LogManager.getLogger(AuthServiceImpl.class);

    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Instant lastLoginInstant = new Date().toInstant();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), null)
        );

        String token = jwtService.generateToken(authentication.getName());

        var authResponse = new AuthResponse();

        authResponse.setAccessToken(token);
        authResponse.setExpiresIn(TimeUnit.MILLISECONDS.toChronoUnit()
                .between(jwtService.extractExpiration(token).toInstant(), lastLoginInstant));
        authResponse.setTokenType("Bearer");
        authResponse.setLastLoginAt(ZonedDateTime.from(lastLoginInstant));

//        userCommandProducer.updateUser(loginRequest.getUsername(), authResponse.getLastLoginAt());

        LOGGER.info("Login successfully by {}", loginRequest.getUsername());

        return authResponse;
    }

    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
//        var user = userMapper.toUser(signUpRequest);
//
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//        SignUpResponse signUpResponse = userMapper.toSignUpResponse(userRepository.save(user));
//
//        LOGGER.info("Sign up successfully by {}", signUpRequest.getUsername());
//
//        // LOGIN IN AFTER SIGN UP
//
//        AuthResponse loginResponse = login(new LoginRequest(signUpRequest.getUsername(), signUpRequest.getPassword()));
//
//        signUpResponse.setAccessToken(loginResponse.getAccessToken());
//        signUpResponse.setLastLoginAt(loginResponse.getLastLoginAt());
//        signUpResponse.setExpiresIn(loginResponse.getExpiresIn());
//        signUpResponse.setTokenType(loginResponse.getTokenType());

        return new SignUpResponse();
    }
}
