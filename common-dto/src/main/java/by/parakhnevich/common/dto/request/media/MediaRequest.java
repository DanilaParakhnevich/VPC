package by.parakhnevich.common.dto.request.media;

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
public class MediaRequest {
    private String userId;
    private OperationType operationType;
    private MediaType kind;
    private String fileName;

    public enum OperationType {
        GET_UPLOAD_URL,
        GET_DOWNLOAD_URL
    }

    public enum MediaType {
        MUSIC,
        LYRICS
    }
}
