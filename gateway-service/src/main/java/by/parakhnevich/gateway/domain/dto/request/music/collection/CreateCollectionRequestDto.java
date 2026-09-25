package by.parakhnevich.gateway.domain.dto.request.music.collection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating a collection")
public class CreateCollectionRequestDto {

    @Schema(example = "Deliverance")
    private String title;
    @Schema(example = "Studio album, 2002")
    private String description;
    @Schema(example = "col-a3f8b2c1")
    private String imageId;

    @Schema(description = "Collection type", example = "ALBUM",
            allowableValues = {"ALBUM", "SINGLE_OR_EP", "DEMO_ALBUM"})
    private String collectionType;

    @Schema(description = "Track IDs", example = "[1, 2, 3]")
    private List<Long> trackIds;
}