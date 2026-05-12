package by.parakhnevich.user.mapper;

import by.parakhnevich.user.domain.dto.response.UserResponse;
import by.parakhnevich.user.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
