package by.parakhnevich.gateway.domain.dto.request.music.collection;

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
public class UpdateCollectionRequestDto {

    @Schema(description = "Collection ID", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(example = "Deliverance")
    private String title;
    @Schema(example = "Updated description")
    private String description;
    @Schema(example = "col-a3f8b2c1")
    private String imageId;

    @Schema(example = "ALBUM",
            allowableValues = {"ALBUM", "SINGLE_OR_EP", "DEMO_ALBUM"})
    private String collectionType;

    private List<Long> trackIds;
}