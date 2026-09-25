package by.parakhnevich.gateway.domain.dto.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

/**
 * Created by agallochum on 2026-05-12
 */
@EqualsAndHashCode
@Data
@SuperBuilder
@NoArgsConstructor
public class UserResponseDto {

    private Long id;
    private String username;
    private String email;
    private String avatarUrl;

    private String role;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime lastLoginAt;

}
