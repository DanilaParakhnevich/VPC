package by.parakhnevich.dto.response;

import lombok.*;

import java.time.ZonedDateTime;

import static by.parakhnevich.dto.response.UserResponse.ErrorMessage.NONE;
import static by.parakhnevich.dto.response.UserResponse.Status.OK;

/**
 * Created by agallochum on 2026-05-16
 */
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String requestId;
    private String userId;
    private String username;
    private String email;
    private String role;
    private String accessToken;
    private String avatarUrl;
    @Builder.Default
    private Status status = OK;
    @Builder.Default
    private ErrorMessage errorMessage = NONE;
    @Builder.Default
    private ZonedDateTime dateTime = ZonedDateTime.now();
    private ZonedDateTime lastLoginAt;
    private ZonedDateTime createdAt;

    public enum Status {
        OK,
        ERROR
    }

    @Getter
    public enum ErrorMessage {
        BAD_TOKEN("${bad_token}"),
        NOT_FOUND("${user_not_found}"),
        BAD_PASSWORD("${user_bad_password}"),
        BAD_REQUEST("$(bad_request)"),
        ALREADY_EXISTS("$(already_exists)"),
        TIMEOUT("${timeout}"),
        NONE("${ok}");

        private final String message;

        ErrorMessage(String message) {
            this.message = message;
        }
    }
}
