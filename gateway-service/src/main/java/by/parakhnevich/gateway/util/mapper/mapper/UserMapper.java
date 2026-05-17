package by.parakhnevich.gateway.util.mapper.mapper;

import by.parakhnevich.gateway.domain.dto.request.LoginRequest;
import by.parakhnevich.gateway.domain.dto.response.AuthResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // FROM USER MAP FUNCTIONS


    AuthResponse toAuthResponse(LoginRequest user);
}
