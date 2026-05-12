package by.parakhnevich.user.service;

import by.parakhnevich.user.domain.dto.request.LoginRequest;
import by.parakhnevich.user.domain.dto.request.SignUpRequest;
import by.parakhnevich.user.domain.dto.response.AuthResponse;
import by.parakhnevich.user.domain.dto.response.SignUpResponse;
import by.parakhnevich.user.domain.dto.response.UserResponse;
import by.parakhnevich.user.domain.entity.User;

public interface AuthService {

    AuthResponse login(LoginRequest loginRequest);

    SignUpResponse signUp(SignUpRequest signUpRequest);
}
