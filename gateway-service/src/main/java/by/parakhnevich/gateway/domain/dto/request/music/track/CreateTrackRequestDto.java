package by.parakhnevich.gateway.domain.dto.request.music.track;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Payload for creating a track")
public class CreateTrackRequestDto {

    @Schema(example = "Deliverance")          private String title;
    @Schema(example = "trk-a3f8b2c1")         private String trackPathId;
    @Schema(description = "Duration in seconds", example = "825")
    private int duration;

    @Schema(description = "Band IDs", example = "[1, 2]")
    private List<Long> bandIds;

    @Schema(description = "Collection IDs", example = "[10]")
    private List<Long> collectionIds;

    @Schema(description = "Genre IDs", example = "[3, 5]")
    private List<Long> genreIds;
}