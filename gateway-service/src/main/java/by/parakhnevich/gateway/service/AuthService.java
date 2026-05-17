package by.parakhnevich.gateway.service;

import by.parakhnevich.gateway.domain.dto.request.LoginRequest;
import by.parakhnevich.gateway.domain.dto.request.SignUpRequest;
import by.parakhnevich.gateway.domain.dto.response.AuthResponse;
import by.parakhnevich.gateway.domain.dto.response.SignUpResponse;

public interface AuthService {

    AuthResponse login(LoginRequest loginRequest);

    SignUpResponse signUp(SignUpRequest signUpRequest);
}
