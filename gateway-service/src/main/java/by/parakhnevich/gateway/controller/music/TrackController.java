package by.parakhnevich.gateway.controller.music;

import by.parakhnevich.common.dto.request.music.TrackRequest;
import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.gateway.domain.dto.request.music.track.CreateTrackRequestDto;
import by.parakhnevich.gateway.domain.dto.request.music.track.UpdateTrackRequestDto;
import by.parakhnevich.gateway.kafka.producer.MusicRequestProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by agallochum on 2026-09-25
 */
@Tag(name = "Tracks", description = "Track management operations")
@RequestMapping("/api/tracks")
@RestController
public class TrackController extends BaseMusicController {

    private static final long HTTP_TIMEOUT_SECONDS = 6;

    private final ObjectMapper objectMapper;

    public TrackController(MusicRequestProducer producer, ObjectMapper objectMapper) {
        super(producer);
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "Create track")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created",
                    content = @Content(schema = @Schema(implementation = MusicResponse.TrackSingle.class))),
            @ApiResponse(responseCode = "504", description = "music-service timeout", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error", content = @Content)
    })
    @PostMapping
    public org.springframework.http.ResponseEntity<? extends MusicResponse> create(
            @RequestBody CreateTrackRequestDto dto) {
        try {
            var req = TrackRequest.Create.builder()
                    .title(dto.getTitle())
                    .trackPathId(dto.getTrackPathId())
                    .duration(dto.getDuration())
                    .bandIds(dto.getBandIds())
                    .collectionIds(dto.getCollectionIds())
                    .genreIds(dto.getGenreIds())
                    .build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("createTrack", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Get track by ID")
    @GetMapping("/{id}")
    public org.springframework.http.ResponseEntity<? extends MusicResponse> getById(@PathVariable Long id) {
        try {
            var req = TrackRequest.GetById.builder().id(id).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("getTrack(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Get tracks page")
    @GetMapping({"", "/"})
    public org.springframework.http.ResponseEntity<? extends MusicResponse> getAll(
            @Parameter(example = "0")  @RequestParam(defaultValue = "0")  int page,
            @Parameter(example = "20") @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String titleLike,
            @RequestParam(required = false) Long bandId,
            @RequestParam(required = false) Long genreId) {
        try {
            var req = TrackRequest.GetAll.builder()
                    .page(page).size(size)
                    .titleLike(titleLike)
                    .bandId(bandId).genreId(genreId)
                    .build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("getAllTracks", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Update track (partial)")
    @PutMapping("/{id}")
    public org.springframework.http.ResponseEntity<? extends MusicResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateTrackRequestDto dto) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> updates = objectMapper.convertValue(dto, Map.class);
            var req = TrackRequest.Update.builder().id(id).updates(updates).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("updateTrack(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Delete track")
    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<? extends MusicResponse> delete(@PathVariable Long id) {
        try {
            var req = TrackRequest.Delete.builder().id(id).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("deleteTrack(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }
}