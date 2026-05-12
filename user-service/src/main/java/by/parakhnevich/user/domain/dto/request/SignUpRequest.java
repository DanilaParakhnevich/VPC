package by.parakhnevich.user.domain.dto.request;

import lombok.Getter;

/**
 * Created by agallochum on 2026-05-12
 */
@Getter
public class SignUpRequest {
    private String username;
    private String email;
    private String password;
}