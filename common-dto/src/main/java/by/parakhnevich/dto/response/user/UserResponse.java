package by.parakhnevich.dto.response.user;

import lombok.*;

import java.time.ZonedDateTime;

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
    private ErrorMessage errorMessage = ErrorMessage.NONE;
    @Builder.Default
    private ZonedDateTime dateTime = ZonedDateTime.now();
    private ZonedDateTime lastLoginAt;
    private ZonedDateTime createdAt;

    @Getter
    public enum ErrorMessage {
        NOT_FOUND(404),
        BAD_PASSWORD(400),
        BAD_REQUEST(400),
        ALREADY_EXISTS(409),
        TIMEOUT(408),
        NONE(200);

        private final int code;

        ErrorMessage(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
