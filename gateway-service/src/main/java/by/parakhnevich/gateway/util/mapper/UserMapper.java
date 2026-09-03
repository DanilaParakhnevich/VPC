package by.parakhnevich.gateway.util.mapper;

import by.parakhnevich.dto.response.user.UserResponse;
import by.parakhnevich.gateway.domain.dto.response.AuthResponseDto;
import by.parakhnevich.gateway.domain.dto.response.SignUpResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    SignUpResponseDto toSignUpResponse(UserResponse userResponse);

    AuthResponseDto toAuthResponse(UserResponse userResponse);

}
