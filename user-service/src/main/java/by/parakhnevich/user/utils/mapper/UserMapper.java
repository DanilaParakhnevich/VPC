package by.parakhnevich.user.utils.mapper;

import by.parakhnevich.common.dto.request.user.UserRequest;
import by.parakhnevich.common.dto.response.user.UserResponse;
import by.parakhnevich.user.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Created by agallochum on 2026-05-20
 */
@Mapper(componentModel = "cdi")
public interface UserMapper {
    @Mapping(source = "userId", target = "id")
    User toUser(UserRequest userRequest);

    @Mapping(source = "id", target = "userId")
    UserResponse toUserResponse(User user);
}
