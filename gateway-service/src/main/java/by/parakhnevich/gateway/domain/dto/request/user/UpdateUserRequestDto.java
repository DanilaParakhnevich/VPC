package by.parakhnevich.gateway.domain.dto.request.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Created by agallochum on 2026-05-23
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Fields to update. Only provided fields are changed; others stay untouched.")
public class UpdateUserRequestDto {

    @Schema(description = "New username", example = "user")
    private String username;

    @Schema(description = "New email", example = "user@example.com")
    private String email;

    @Schema(description = "New password (plain text, will be hashed server-side)",
            example = "s3cr3t!", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Schema(description = "Avatar URL", example = "https://cdn.example.com/avatar.png")
    private String avatarUrl;

    @Schema(description = "Role", example = "USER", allowableValues = {"USER", "ADMIN", "MODERATOR"})
    private String role;
}