package by.parakhnevich.user.domain.dto.response;

import by.parakhnevich.user.domain.entity.AccountStatus;
import by.parakhnevich.user.domain.entity.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by agallochum on 2026-05-12
 */
@EqualsAndHashCode
@Data
@SuperBuilder
@NoArgsConstructor
@Builder
public class UserResponse {

    private UUID id;
    private String username;
    private String email;
    private String avatarUrl;

    private Role role;
    private AccountStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime lastLoginAt;

}
