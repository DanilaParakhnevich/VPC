package by.parakhnevich.gateway.domain.dto.request.music.band;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating a band")
public class CreateBandRequestDto {

    @Schema(example = "Opeth")
    private String name;

    @Schema(example = "Swedish progressive metal band")
    private String description;

    @Schema(example = "SE")
    private String geo;

    @Schema(description = "Media-service image ID", example = "a3f8b2c1")
    private String imageId;
}