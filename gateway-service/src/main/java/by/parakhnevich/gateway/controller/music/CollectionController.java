package by.parakhnevich.gateway.controller.music;

import by.parakhnevich.common.dto.request.music.CollectionRequest;
import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.gateway.domain.dto.request.music.collection.CreateCollectionRequestDto;
import by.parakhnevich.gateway.domain.dto.request.music.collection.UpdateCollectionRequestDto;
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
@Tag(name = "Collections", description = "Collection (album / EP / demo) management operations")
@RequestMapping("/api/collections")
@RestController
public class CollectionController extends BaseMusicController {

    private static final long HTTP_TIMEOUT_SECONDS = 6;

    private final ObjectMapper objectMapper;

    public CollectionController(MusicRequestProducer producer, ObjectMapper objectMapper) {
        super(producer);
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "Create collection")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created",
                    content = @Content(schema = @Schema(implementation = MusicResponse.CollectionSingle.class))),
            @ApiResponse(responseCode = "400", description = "Unknown collection type", content = @Content),
            @ApiResponse(responseCode = "504", description = "music-service timeout", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<? extends MusicResponse> create(@RequestBody CreateCollectionRequestDto dto) {
        try {
            var req = CollectionRequest.Create.builder()
                    .title(dto.getTitle())
                    .description(dto.getDescription())
                    .imageId(dto.getImageId())
                    .collectionType(dto.getCollectionType())
                    .trackIds(dto.getTrackIds())
                    .build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("createCollection", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Get collection by ID")
    @GetMapping("/{id}")
    public ResponseEntity<? extends MusicResponse> getById(@PathVariable Long id) {
        try {
            var req = CollectionRequest.GetById.builder().id(id).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("getCollection(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Get collections page")
    @GetMapping({"", "/"})
    public ResponseEntity<? extends MusicResponse> getAll(
            @Parameter(example = "0")  @RequestParam(defaultValue = "0")  int page,
            @Parameter(example = "20") @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String titleLike,
            @RequestParam(required = false) String collectionType) {
        try {
            var req = CollectionRequest.GetAll.builder()
                    .page(page).size(size)
                    .titleLike(titleLike)
                    .collectionType(collectionType)
                    .build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("getAllCollections", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Update collection (partial)")
    @PutMapping("/{id}")
    public ResponseEntity<? extends MusicResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateCollectionRequestDto dto) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> updates = objectMapper.convertValue(dto, Map.class);
            var req = CollectionRequest.Update.builder().id(id).updates(updates).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("updateCollection(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Delete collection")
    @DeleteMapping("/{id}")
    public ResponseEntity<? extends MusicResponse> delete(@PathVariable Long id) {
        try {
            var req = CollectionRequest.Delete.builder().id(id).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("deleteCollection(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }
}