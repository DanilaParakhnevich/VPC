package by.parakhnevich.gateway.domain.dto.request.music.band;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Partial update — only provided fields change")
public class UpdateBandRequestDto {

    @Schema(description = "Band ID", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
    @Schema(example = "Opeth")
    private String name;
    @Schema(example = "Progressive metal")
    private String description;
    @Schema(example = "SE")
    private String geo;
    @Schema(example = "a3f8b2c1")
    private String imageId;
}