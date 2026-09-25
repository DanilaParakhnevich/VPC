package by.parakhnevich.gateway.controller.music;

import by.parakhnevich.common.dto.request.music.MusicianRequest;
import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.gateway.domain.dto.request.music.musician.CreateMusicianRequestDto;
import by.parakhnevich.gateway.domain.dto.request.music.musician.UpdateMusicianRequestDto;
import by.parakhnevich.gateway.kafka.producer.MusicRequestProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by agallochum on 2026-09-25
 */
@Tag(name = "Musicians", description = "Musician management operations")
@RequestMapping("/api/musicians")
@RestController
public class MusicianController extends BaseMusicController {

    private static final long HTTP_TIMEOUT_SECONDS = 6;

    private final ObjectMapper objectMapper;

    public MusicianController(MusicRequestProducer producer, ObjectMapper objectMapper) {
        super(producer);
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "Create musician")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created",
                    content = @Content(schema = @Schema(implementation = MusicResponse.MusicianSingle.class))),
            @ApiResponse(responseCode = "504", description = "music-service timeout", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<? extends MusicResponse> create(@RequestBody CreateMusicianRequestDto dto) {
        try {
            var req = MusicianRequest.Create.builder()
                    .name(dto.getName())
                    .imageId(dto.getImageId())
                    .birthDate(dto.getBirthDate())
                    .build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("createMusician", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Get musician by ID")
    @GetMapping("/{id}")
    public ResponseEntity<? extends MusicResponse> getById(@PathVariable Long id) {
        try {
            var req = MusicianRequest.GetById.builder().id(id).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("getMusician(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Get musicians page")
    @GetMapping({"", "/"})
    public ResponseEntity<? extends MusicResponse> getAll(
            @Parameter(example = "0")  @RequestParam(defaultValue = "0")  int page,
            @Parameter(example = "20") @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String nameLike,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bornAfter,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bornBefore) {
        try {
            var req = MusicianRequest.GetAll.builder()
                    .page(page).size(size)
                    .nameLike(nameLike)
                    .build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("getAllMusicians", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Update musician (partial)")
    @PutMapping("/{id}")
    public ResponseEntity<? extends MusicResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateMusicianRequestDto dto) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> updates = objectMapper.convertValue(dto, Map.class);
            var req = MusicianRequest.Update.builder().id(id).updates(updates).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("updateMusician(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Delete musician")
    @DeleteMapping("/{id}")
    public ResponseEntity<? extends MusicResponse> delete(@PathVariable Long id) {
        try {
            var req = MusicianRequest.Delete.builder().id(id).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("deleteMusician(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }
}