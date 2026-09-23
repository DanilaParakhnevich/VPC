package by.parakhnevich.media.redis.key;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaKey {
    private String path;
    private String op;
    private String userId;
    private String contentType;
    private long createdAt;

    public MediaKey() {
    }
}
