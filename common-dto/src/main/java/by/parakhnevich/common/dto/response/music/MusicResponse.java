package by.parakhnevich.common.dto.response.music;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by agallochum on 2026-09-24
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "responseType", visible = false)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MusicResponse.Single.class,        name = "SINGLE"),
        @JsonSubTypes.Type(value = MusicResponse.Page.class,          name = "PAGE"),
        @JsonSubTypes.Type(value = MusicResponse.ErrorResponse.class, name = "ERROR"),
})
public sealed interface MusicResponse permits
        MusicResponse.Single,
        MusicResponse.Page,
        MusicResponse.ErrorResponse {

    String requestId();

    ResponseCode responseCode();

    LocalDateTime dateTime();

    @JsonIgnore
    default boolean isSuccess() {
        return responseCode() == ResponseCode.OK;
    }

    @Getter
    enum ResponseCode {
        NOT_FOUND(404),
        BAD_REQUEST(400),
        ALREADY_EXISTS(409),
        TIMEOUT(408),
        OK(200);

        private final int code;

        ResponseCode(int code) { this.code = code; }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "entity", visible = false)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MusicResponse.BandSingle.class,       name = "BAND"),
            @JsonSubTypes.Type(value = MusicResponse.TrackSingle.class,      name = "TRACK"),
            @JsonSubTypes.Type(value = MusicResponse.GenreSingle.class,      name = "GENRE"),
            @JsonSubTypes.Type(value = MusicResponse.MusicianSingle.class,   name = "MUSICIAN"),
            @JsonSubTypes.Type(value = MusicResponse.CollectionSingle.class, name = "COLLECTION"),
    })
    sealed interface Single extends MusicResponse permits BandSingle, CollectionSingle, Deleted, GenreSingle, MusicianSingle, TrackSingle {

        @JsonIgnore
        String entity();
    }

    @Builder @With
    record BandSingle(
            String requestId,
            Long id,
            String name,
            String description,
            String geo,
            String imageId,
            List<Long> musicianIds,
            List<Long> genreIds,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            ResponseCode responseCode,
            LocalDateTime dateTime
    ) implements Single {
        public BandSingle {
            if (responseCode == null) responseCode = ResponseCode.OK;
            if (dateTime     == null) dateTime     = LocalDateTime.now();
            if (musicianIds  == null) musicianIds  = List.of();
            if (genreIds     == null) genreIds     = List.of();
        }
        @Override public String entity() { return "BAND"; }
    }

    @Builder @With
    record TrackSingle(
            String requestId,
            Long id,
            String title,
            String trackPathId,
            int duration,
            List<Long> bandIds,
            List<Long> collectionIds,
            List<Long> genreIds,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            ResponseCode responseCode,
            LocalDateTime dateTime
    ) implements Single {
        public TrackSingle {
            if (responseCode  == null) responseCode  = ResponseCode.OK;
            if (dateTime      == null) dateTime      = LocalDateTime.now();
            if (bandIds       == null) bandIds       = List.of();
            if (collectionIds == null) collectionIds = List.of();
            if (genreIds      == null) genreIds      = List.of();
        }
        @Override public String entity() { return "TRACK"; }
    }

    @Builder @With
    record GenreSingle(
            String requestId,
            Long id,
            String title,
            String description,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            ResponseCode responseCode,
            LocalDateTime dateTime
    ) implements Single {
        public GenreSingle {
            if (responseCode == null) responseCode = ResponseCode.OK;
            if (dateTime     == null) dateTime     = LocalDateTime.now();
        }
        @Override public String entity() { return "GENRE"; }
    }

    @Builder @With
    record MusicianSingle(
            String requestId,
            Long id,
            String name,
            String imageId,
            LocalDate birthDate,
            List<Long> bandIds,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            ResponseCode responseCode,
            LocalDateTime dateTime
    ) implements Single {
        public MusicianSingle {
            if (responseCode == null) responseCode = ResponseCode.OK;
            if (dateTime     == null) dateTime     = LocalDateTime.now();
            if (bandIds      == null) bandIds      = List.of();
        }
        @Override public String entity() { return "MUSICIAN"; }
    }

    @Builder @With
    record CollectionSingle(
            String requestId,
            Long id,
            String title,
            String description,
            String imageId,
            String collectionType,     // ALBUM / SINGLE_OR_EP / DEMO_ALBUM
            List<Long> trackIds,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            ResponseCode responseCode,
            LocalDateTime dateTime
    ) implements Single {
        public CollectionSingle {
            if (responseCode == null) responseCode = ResponseCode.OK;
            if (dateTime     == null) dateTime     = LocalDateTime.now();
            if (trackIds     == null) trackIds     = List.of();
        }
        @Override public String entity() { return "COLLECTION"; }
    }

    @Builder @With
    record Deleted(
            String requestId,
            Long id,
            String entity,
            String description,
            ResponseCode responseCode,
            LocalDateTime dateTime
    ) implements Single {
        public Deleted {
            if (responseCode == null) responseCode = ResponseCode.OK;
            if (dateTime     == null) dateTime     = LocalDateTime.now();
        }
    }

    @Builder @With
    record Page(
            String requestId,
            List<Single> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            ResponseCode responseCode,
            LocalDateTime dateTime
    ) implements MusicResponse {
        public Page {
            if (responseCode == null) responseCode = ResponseCode.OK;
            if (dateTime     == null) dateTime     = LocalDateTime.now();
            if (content      == null) content      = List.of();
        }
    }

    @Builder @With
    record ErrorResponse(
            String requestId,
            ResponseCode responseCode,
            String message,
            LocalDateTime dateTime
    ) implements MusicResponse {
        public ErrorResponse {
            if (responseCode == null) responseCode = ResponseCode.BAD_REQUEST;
            if (dateTime     == null) dateTime     = LocalDateTime.now();
        }
    }
}