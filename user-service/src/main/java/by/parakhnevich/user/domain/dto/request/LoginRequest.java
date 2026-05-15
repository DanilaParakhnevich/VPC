package by.parakhnevich.user.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by agallochum on 2026-05-12
 */
@Getter
@AllArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
}
