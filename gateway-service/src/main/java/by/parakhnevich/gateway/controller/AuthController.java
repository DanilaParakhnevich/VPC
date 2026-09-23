package by.parakhnevich.gateway.controller;

import by.parakhnevich.common.dto.response.user.UserResponse;
import by.parakhnevich.gateway.domain.dto.request.LoginRequestDto;
import by.parakhnevich.gateway.domain.dto.request.SignUpRequestDto;
import by.parakhnevich.gateway.domain.dto.response.AuthResponseDto;
import by.parakhnevich.gateway.domain.dto.response.SignUpResponseDto;
import by.parakhnevich.gateway.kafka.producer.UserRequestProducer;
import by.parakhnevich.gateway.util.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by agallochum on 2026-05-12
 */
@RequestMapping("/api/auth")
@RestController
@AllArgsConstructor
public class AuthController {

    private static final Logger LOGGER = LogManager.getLogger(AuthController.class);
    private static final long HTTP_TIMEOUT_SECONDS = 6;

    private final UserRequestProducer userRequestProducer;
    private final UserMapper userMapper;

    @Operation(summary = "Register user", description = "Returns user data or an error")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "409", description = "Already exists"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PostMapping("/register")
    public ResponseEntity<SignUpResponseDto> register(@RequestBody SignUpRequestDto signUpRequest) {
        try {
            var response = userRequestProducer
                    .register(signUpRequest.getEmail(),
                            signUpRequest.getUsername(),
                            signUpRequest.getPassword())
                    .get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (response instanceof UserResponse.Single s && s.isSuccess()) {
                return ResponseEntity.ok(userMapper.toSignUpResponse(s));
            }
            return ResponseEntity.status(response.errorMessage().getCode()).build();
        } catch (TimeoutException e) {
            LOGGER.error("Timeout for register", e);
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Authenticate user", description = "Returns authenticated user with token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            var response = userRequestProducer
                    .authenticate(loginRequest.getUsername(), loginRequest.getPassword())
                    .get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (response instanceof UserResponse.Single s && s.isSuccess()) {
                return ResponseEntity.ok(userMapper.toAuthResponse(s));
            }
            return ResponseEntity.status(response.errorMessage().getCode()).build();
        } catch (TimeoutException e) {
            LOGGER.error("Timeout for login", e);
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}