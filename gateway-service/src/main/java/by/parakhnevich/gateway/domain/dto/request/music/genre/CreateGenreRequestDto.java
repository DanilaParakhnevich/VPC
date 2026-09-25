package by.parakhnevich.gateway.domain.dto.request.music.genre;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating a genre")
public class CreateGenreRequestDto {

    @Schema(example = "Progressive Metal")
    private String title;
    @Schema(example = "Heavy, technical, with odd time signatures")
    private String description;
}