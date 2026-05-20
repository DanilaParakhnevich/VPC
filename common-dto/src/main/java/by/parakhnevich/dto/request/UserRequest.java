package by.parakhnevich.dto.request;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Created by agallochum on 2026-05-16
 */
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String requestId;
    private String email;
    private String userId;
    private String username;
    private String password;
    private String token;
    private Action action;
    private Map<String, Object> updates;
    @Builder.Default
    private ZonedDateTime dateTime = ZonedDateTime.now();

    public enum Action {
        REGISTER,
        AUTHENTICATE,
        VALIDATE,
        GET_USER_BY_USERNAME,
        UPDATE_USER,
        GET_USER_BY_ID
    }
}
