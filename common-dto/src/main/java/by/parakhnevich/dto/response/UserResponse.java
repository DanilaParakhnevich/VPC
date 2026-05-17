package by.parakhnevich.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

import static by.parakhnevich.dto.response.UserResponse.ErrorMessage.NONE;
import static by.parakhnevich.dto.response.UserResponse.Status.OK;

/**
 * Created by agallochum on 2026-05-16
 */
@Getter
@Setter
@Builder
public class UserResponse {
    private String requestId;
    private String userId;
    private String username;
    private String email;
    private String role;
    private Status status = OK;
    private ErrorMessage errorMessage = NONE;
    private ZonedDateTime dateTime = ZonedDateTime.now();

    public enum Status {
        OK,
        NOT_FOUND,
        VALIDATION_ERROR
    }

    @Getter
    public enum ErrorMessage {
        NOT_FOUND("${user_not_found}"),
        BAD_PASSWORD("${user_bad_password}"),
        TIMEOUT("${timeout}"),
        NONE("${ok}");

        private final String message;

        ErrorMessage(String message) {
            this.message = message;
        }
    }
}
