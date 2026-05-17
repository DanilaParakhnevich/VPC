package by.parakhnevich.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Created by agallochum on 2026-05-16
 */
@Getter
@Setter
@Builder
public class UserRequest {
    private String requestId;
    private String userId;
    private String username;
    private String password;
    private String token;
    private Action action;
    Map<String, Object> updates;
    private ZonedDateTime dateTime = ZonedDateTime.now();

    public enum Action {
        VALIDATE,
        AUTHENTICATE,
        GET_USER_BY_USERNAME, UPDATE_USER, GET_USER_BY_ID
    }
}
