package by.parakhnevich.gateway.controller.music;

import by.parakhnevich.common.dto.request.music.GenreRequest;
import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.gateway.domain.dto.request.music.genre.CreateGenreRequestDto;
import by.parakhnevich.gateway.domain.dto.request.music.genre.UpdateGenreRequestDto;
import by.parakhnevich.gateway.kafka.producer.MusicRequestProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by agallochum on 2026-09-25
 */
@Tag(name = "Genres", description = "Genre management operations")
@RequestMapping("/api/genres")
@RestController
public class GenreController extends BaseMusicController {

    private static final long HTTP_TIMEOUT_SECONDS = 6;

    private final ObjectMapper objectMapper;

    public GenreController(MusicRequestProducer producer, ObjectMapper objectMapper) {
        super(producer);
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "Create genre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created",
                    content = @Content(schema = @Schema(implementation = MusicResponse.GenreSingle.class))),
            @ApiResponse(responseCode = "409", description = "Already exists", content = @Content),
            @ApiResponse(responseCode = "504", description = "music-service timeout", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<? extends MusicResponse> create(@RequestBody CreateGenreRequestDto dto) {
        try {
            var req = GenreRequest.Create.builder()
                    .title(dto.getTitle())
                    .description(dto.getDescription())
                    .build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("createGenre", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Get genre by ID")
    @GetMapping("/{id}")
    public ResponseEntity<? extends MusicResponse> getById(@PathVariable Long id) {
        try {
            var req = GenreRequest.GetById.builder().id(id).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("getGenre(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Get genres page")
    @GetMapping({"", "/"})
    public ResponseEntity<? extends MusicResponse> getAll(
            @Parameter(example = "0")  @RequestParam(defaultValue = "0")  int page,
            @Parameter(example = "20") @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String titleLike) {
        try {
            var req = GenreRequest.GetAll.builder()
                    .page(page).size(size).titleLike(titleLike)
                    .build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("getAllGenres", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Update genre (partial)")
    @PutMapping("/{id}")
    public ResponseEntity<? extends MusicResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateGenreRequestDto dto) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> updates = objectMapper.convertValue(dto, Map.class);
            var req = GenreRequest.Update.builder().id(id).updates(updates).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("updateGenre(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Delete genre")
    @DeleteMapping("/{id}")
    public ResponseEntity<? extends MusicResponse> delete(@PathVariable Long id) {
        try {
            var req = GenreRequest.Delete.builder().id(id).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("deleteGenre(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }
}