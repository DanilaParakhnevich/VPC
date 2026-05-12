package by.parakhnevich.user.mapper;

import by.parakhnevich.user.domain.dto.request.LoginRequest;
import by.parakhnevich.user.domain.dto.request.SignUpRequest;
import by.parakhnevich.user.domain.dto.response.AuthResponse;
import by.parakhnevich.user.domain.dto.response.SignUpResponse;
import by.parakhnevich.user.domain.dto.response.UserResponse;
import by.parakhnevich.user.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // FROM USER MAP FUNCTIONS

    UserResponse toUserResponse(User user);

    AuthResponse toAuthResponse(User user);

    SignUpResponse toSignUpResponse(User user);


    // TO USER MAP FUNCTIONS

    User toUser(SignUpRequest signUpRequest);

    User toUser(LoginRequest loginRequest);

}
