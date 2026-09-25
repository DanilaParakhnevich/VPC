package by.parakhnevich.gateway.domain.dto.request.music.genre;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateGenreRequestDto {

    @Schema(description = "Genre ID", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(example = "Progressive Metal")
    private String title;
    @Schema(example = "Updated description")
    private String description;
}