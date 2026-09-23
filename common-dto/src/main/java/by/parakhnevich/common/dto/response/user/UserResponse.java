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
        @JsonSubTypes.Type(value = UserResponse.Page.class,          name = "Page"),
        @JsonSubTypes.Type(value = UserResponse.ErrorResponse.class, name = "ERROR"),
})
public sealed interface UserResponse permits
        UserResponse.Single,
        UserResponse.Page,
        UserResponse.ErrorResponse {

    String requestId();

    ResponseCode responseCode();

    ZonedDateTime dateTime();

    @JsonIgnore
    default boolean isSuccess() {
        return responseCode() == ResponseCode.OK;
    }

    @Getter
    enum ResponseCode {
        NOT_FOUND(404),
        BAD_PASSWORD(400),
        BAD_REQUEST(400),
        ALREADY_EXISTS(409),
        TIMEOUT(408),
        OK(200);

        private final int code;

        ResponseCode(int code) { this.code = code; }
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
            ResponseCode responseCode,
            ZonedDateTime dateTime,
            ZonedDateTime lastLoginAt,
            ZonedDateTime createdAt
    ) implements UserResponse {
        public Single {
            if (responseCode == null) responseCode = ResponseCode.OK;
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
            ResponseCode responseCode,
            ZonedDateTime dateTime
    ) implements UserResponse {
        public Page {
            if (responseCode == null) responseCode = ResponseCode.OK;
            if (dateTime     == null) dateTime     = ZonedDateTime.now();
            if (content      == null) content      = java.util.List.of();
        }
    }

    @Builder @With
    record ErrorResponse(
            String requestId,
            ResponseCode responseCode,
            String message,
            ZonedDateTime dateTime
    ) implements UserResponse {
        public ErrorResponse {
            if (responseCode == null) responseCode = ResponseCode.BAD_REQUEST;
            if (dateTime     == null) dateTime     = ZonedDateTime.now();
        }
    }
}