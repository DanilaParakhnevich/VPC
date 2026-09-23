package by.parakhnevich.common.dto.request.user;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.With;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;


/**
 * Created by agallochum on 2026-05-16
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "action", visible = false)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserRequest.Register.class,      name = "REGISTER"),
        @JsonSubTypes.Type(value = UserRequest.Authenticate.class,  name = "AUTHENTICATE"),
        @JsonSubTypes.Type(value = UserRequest.GetById.class,       name = "GET_BY_ID"),
        @JsonSubTypes.Type(value = UserRequest.GetByUsername.class, name = "GET_BY_USERNAME"),
        @JsonSubTypes.Type(value = UserRequest.GetAll.class,    name = "GET_ALL"),
        @JsonSubTypes.Type(value = UserRequest.Update.class,        name = "UPDATE"),
})
public sealed interface UserRequest permits
        UserRequest.Register,
        UserRequest.Authenticate,
        UserRequest.GetById,
        UserRequest.GetByUsername,
        UserRequest.GetAll,
        UserRequest.Update {

    String requestId();

    ZonedDateTime dateTime();

    @Builder @With
    record Register(
            String requestId,
            String email,
            String username,
            String password,
            ZonedDateTime dateTime
    ) implements UserRequest {
        public Register {
            if (requestId == null) requestId = UUID.randomUUID().toString();
            if (dateTime  == null) dateTime  = ZonedDateTime.now();
        }
    }

    @Builder @With
    record Authenticate(
            String requestId,
            String username,
            String password,
            ZonedDateTime dateTime
    ) implements UserRequest {
        public Authenticate {
            if (requestId == null) requestId = UUID.randomUUID().toString();
            if (dateTime  == null) dateTime  = ZonedDateTime.now();
        }
    }

    @Builder @With
    record GetById(
            String requestId,
            String userId,
            ZonedDateTime dateTime
    ) implements UserRequest {
        public GetById {
            if (requestId == null) requestId = UUID.randomUUID().toString();
            if (dateTime  == null) dateTime  = ZonedDateTime.now();
        }
    }

    @Builder @With
    record GetByUsername(
            String requestId,
            String username,
            ZonedDateTime dateTime
    ) implements UserRequest {
        public GetByUsername {
            if (requestId == null) requestId = UUID.randomUUID().toString();
            if (dateTime  == null) dateTime  = ZonedDateTime.now();
        }
    }

    @Builder @With
    record GetAll(
            String requestId,
            Integer page,
            Integer size,
            String emailLike,
            String usernameLike,
            ZonedDateTime createdAfter,
            ZonedDateTime createdBefore,
            ZonedDateTime dateTime
    ) implements UserRequest {
        public GetAll {
            if (requestId == null) requestId = UUID.randomUUID().toString();
            if (dateTime  == null) dateTime  = ZonedDateTime.now();
            if (page      == null) page      = 0;
            if (size      == null || size <= 0) size = 20;
        }
    }

    @Builder @With
    record Update(
            String requestId,
            String userId,
            Map<String, Object> updates,
            ZonedDateTime dateTime
    ) implements UserRequest {
        public Update {
            if (requestId == null) requestId = UUID.randomUUID().toString();
            if (dateTime  == null) dateTime  = ZonedDateTime.now();
        }
    }
}