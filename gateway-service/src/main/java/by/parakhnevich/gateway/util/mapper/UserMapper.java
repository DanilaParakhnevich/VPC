package by.parakhnevich.gateway.util.mapper;

import by.parakhnevich.common.dto.response.user.UserResponse;
import by.parakhnevich.gateway.domain.dto.response.user.AuthResponseDto;
import by.parakhnevich.gateway.domain.dto.response.user.SignUpResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Created by agallochum on 2026-05-12
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "userId", target = "id")
    @Mapping(target = "status", ignore = true)
    SignUpResponseDto toSignUpResponse(UserResponse.Single userResponse);

    @Mapping(source = "userId", target = "id")
    @Mapping(target = "status", ignore = true)
    AuthResponseDto toAuthResponse(UserResponse.Single userResponse);
}