package by.parakhnevich.gateway.domain.dto.request.music.musician;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating a musician")
public class CreateMusicianRequestDto {

    @Schema(example = "Mikael ?kerfeldt")
    private String name;
    @Schema(example = "mus-a3f8b2c1")
    private String imageId;

    @Schema(description = "Birth date (ISO-8601)", example = "1974-04-17")
    private LocalDate birthDate;
}