package by.parakhnevich.common.dto.response.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by agallochum on 2026-05-16
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "responseType", visible = false)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserResponse.Single.class,        name = "SINGLE"),
        @JsonSubTypes.Type(value = UserResponse.Page.class,          name = "PAGE"),
        @JsonSubTypes.Type(value = UserResponse.ErrorResponse.class, name = "ERROR"),
})
public sealed interface UserResponse permits
        UserResponse.Single,
        UserResponse.Page,
        UserResponse.ErrorResponse {

    String requestId();

    ErrorMessage errorMessage();

    ZonedDateTime dateTime();

    @JsonIgnore
    default boolean isSuccess() {
        return errorMessage() == ErrorMessage.NONE;
    }

    @Getter
    enum ErrorMessage {
        NOT_FOUND(404),
        BAD_PASSWORD(400),
        BAD_REQUEST(400),
        ALREADY_EXISTS(409),
        TIMEOUT(408),
        NONE(200);

        private final int code;

        ErrorMessage(int code) { this.code = code; }
    }

    @Builder @With
    record Single(
            String requestId,
            String userId,
            String username,
            String email,
            String role,
            String accessToken,
            String avatarUrl,
            ErrorMessage errorMessage,
            ZonedDateTime dateTime,
            ZonedDateTime lastLoginAt,
            ZonedDateTime createdAt
    ) implements UserResponse {
        public Single {
            if (errorMessage == null) errorMessage = ErrorMessage.NONE;
            if (dateTime     == null) dateTime     = ZonedDateTime.now();
        }
    }

    @Builder @With
    record Page(
            String requestId,
            List<Single> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            ErrorMessage errorMessage,
            ZonedDateTime dateTime
    ) implements UserResponse {
        public Page {
            if (errorMessage == null) errorMessage = ErrorMessage.NONE;
            if (dateTime     == null) dateTime     = ZonedDateTime.now();
            if (content      == null) content      = List.of();
        }
    }

    @Builder @With
    record ErrorResponse(
            String requestId,
            ErrorMessage errorMessage,
            String message,
            ZonedDateTime dateTime
    ) implements UserResponse {
        public ErrorResponse {
            if (errorMessage == null) errorMessage = ErrorMessage.BAD_REQUEST;
            if (dateTime     == null) dateTime     = ZonedDateTime.now();
        }
    }
}