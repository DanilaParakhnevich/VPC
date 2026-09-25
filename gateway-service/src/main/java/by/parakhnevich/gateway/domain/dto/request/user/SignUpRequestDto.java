package by.parakhnevich.gateway.domain.dto.request.user;

import lombok.Getter;

/**
 * Created by agallochum on 2026-05-12
 */
@Getter
public class SignUpRequestDto {
    private String username;
    private String email;
    private String password;
}