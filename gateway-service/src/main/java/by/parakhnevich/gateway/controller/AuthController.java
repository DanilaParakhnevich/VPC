package by.parakhnevich.gateway.controller;

import by.parakhnevich.dto.response.user.UserResponse;
import by.parakhnevich.gateway.domain.dto.request.LoginRequestDto;
import by.parakhnevich.gateway.domain.dto.request.SignUpRequestDto;
import by.parakhnevich.gateway.domain.dto.response.AuthResponseDto;
import by.parakhnevich.gateway.domain.dto.response.SignUpResponseDto;
import by.parakhnevich.gateway.kafka.producer.UserRequestProducer;
import by.parakhnevich.gateway.util.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static by.parakhnevich.dto.response.user.UserResponse.ErrorMessage.*;

/**
 * Created by agallochum on 2026-05-12
 */
@RequestMapping("/api/auth")
@RestController
@AllArgsConstructor
public class AuthController {

    private static final Logger LOGGER = LogManager.getLogger(AuthController.class);

    private UserRequestProducer userRequestProducer;
    
    private UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<SignUpResponseDto> register(@RequestBody SignUpRequestDto signUpRequest) {
        try {
            var register = userRequestProducer.register(signUpRequest.getEmail(), signUpRequest.getUsername(), signUpRequest.getPassword());

            UserResponse userResponse = register.get();
            if (userResponse.getErrorMessage().equals(NONE)) {
                return ResponseEntity.ok(userMapper.toSignUpResponse(userResponse));
            } else if (userResponse.getErrorMessage().equals(ALREADY_EXISTS)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            var login = userRequestProducer.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

            UserResponse userResponse = login.get();
            if (userResponse.getErrorMessage().equals(NONE)) {
                return ResponseEntity.ok(userMapper.toAuthResponse(userResponse));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
