package by.parakhnevich.gateway.domain.dto.request.music.musician;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateMusicianRequestDto {

    @Schema(description = "Musician ID", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(example = "Ivan Ivanov")
    private String name;
    @Schema(example = "mus-a3f8b2c1")
    private String imageId;
    @Schema(example = "1974-04-17")
    private LocalDate birthDate;
}