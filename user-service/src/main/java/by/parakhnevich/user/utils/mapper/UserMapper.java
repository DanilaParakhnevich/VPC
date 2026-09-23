package by.parakhnevich.user.utils.mapper;

import by.parakhnevich.common.dto.request.user.UserRequest;
import by.parakhnevich.common.dto.response.user.UserResponse;
import by.parakhnevich.user.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Created by agallochum on 2026-05-20
 */
@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toUser(UserRequest.Register request);

    @Mapping(source = "id", target = "userId")
    @Mapping(target = "requestId",    ignore = true)
    @Mapping(target = "accessToken",  ignore = true)
    @Mapping(target = "responseCode", ignore = true)
    @Mapping(target = "dateTime",     ignore = true)
    UserResponse.Single toSingle(User user);

    default UserResponse.Single toSingle(String requestId, User user) {
        if (user == null) return null;
        return toSingle(user).withRequestId(requestId);
    }
}