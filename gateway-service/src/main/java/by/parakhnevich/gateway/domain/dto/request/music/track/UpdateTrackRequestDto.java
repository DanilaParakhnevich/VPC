package by.parakhnevich.gateway.domain.dto.request.music.track;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Partial update — only provided fields change")
public class UpdateTrackRequestDto {

    @Schema(description = "Track ID", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(example = "Deliverance")
    private String title;
    @Schema(example = "trk-a3f8b2c1")
    private String trackPathId;
    @Schema(example = "825")
    private Integer duration;
    private List<Long> bandIds;
    private List<Long> collectionIds;
    private List<Long> genreIds;
}