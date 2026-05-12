package by.parakhnevich.user.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Created by agallochum on 2026-05-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
public class SignUpResponse extends UserResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;

}
