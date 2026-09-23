package by.parakhnevich.common.dto.request.music;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.ZonedDateTime;

/**
 * Created by agallochum on 2026-09-23
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "entity", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BandRequest.class,       name = "BAND"),
        @JsonSubTypes.Type(value = TrackRequest.class,      name = "TRACK"),
        @JsonSubTypes.Type(value = GenreRequest.class,      name = "GENRE"),
        @JsonSubTypes.Type(value = MusicianRequest.class,   name = "MUSICIAN"),
        @JsonSubTypes.Type(value = CollectionRequest.class, name = "COLLECTION"),
})
public sealed interface MusicRequest permits BandRequest, TrackRequest, GenreRequest, MusicianRequest, CollectionRequest {

    String requestId();

}
