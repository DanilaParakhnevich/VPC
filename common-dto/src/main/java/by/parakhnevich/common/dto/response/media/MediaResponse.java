package by.parakhnevich.common.dto.response.media;

import lombok.*;

/**
 * Created by agallochum on 2026-09-22
 */
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse {

    private String key;
    private long expiresIn;

}
