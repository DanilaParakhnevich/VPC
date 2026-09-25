package by.parakhnevich.gateway.controller.music;

import by.parakhnevich.common.dto.request.music.BandRequest;
import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.gateway.domain.dto.request.music.band.CreateBandRequestDto;
import by.parakhnevich.gateway.domain.dto.request.music.band.UpdateBandRequestDto;
import by.parakhnevich.gateway.kafka.producer.MusicRequestProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by agallochum on 2026-09-25
 */
@Tag(name = "Bands", description = "Band management operations")
@RequestMapping("/api/bands")
@RestController
public class BandController extends BaseMusicController {

    private static final Logger LOGGER = LogManager.getLogger(BandController.class);
    private static final long HTTP_TIMEOUT_SECONDS = 6;

    private final ObjectMapper objectMapper;

    public BandController(MusicRequestProducer producer, ObjectMapper objectMapper) {
        super(producer);
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "Create band")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created",
                    content = @Content(schema = @Schema(implementation = MusicResponse.BandSingle.class))),
            @ApiResponse(responseCode = "409", description = "Already exists", content = @Content),
            @ApiResponse(responseCode = "504", description = "music-service timeout", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<? extends MusicResponse> create(@RequestBody CreateBandRequestDto dto) {
        try {
            var req = BandRequest.Create.builder()
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .geo(dto.getGeo())
                    .imageId(dto.getImageId())
                    .build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("createBand", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Get band by ID")
    @GetMapping("/{id}")
    public ResponseEntity<? extends MusicResponse> getById(@PathVariable Long id) {
        try {
            var req = BandRequest.GetById.builder().id(id).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("getBand(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Get bands page")
    @GetMapping({"", "/"})
    public ResponseEntity<? extends MusicResponse> getAll(
            @Parameter(example = "0")  @RequestParam(defaultValue = "0")  int page,
            @Parameter(example = "20") @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String nameLike,
            @RequestParam(required = false) String geo) {
        try {
            var req = BandRequest.GetAll.builder()
                    .page(page).size(size).nameLike(nameLike).geo(geo).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("getAllBands", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Update band (partial)")
    @PutMapping("/{id}")
    public ResponseEntity<? extends MusicResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateBandRequestDto dto) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> updates = objectMapper.convertValue(dto, Map.class);
            var req = BandRequest.Update.builder().id(id).updates(updates).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("updateBand(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }

    @Operation(summary = "Delete band")
    @DeleteMapping("/{id}")
    public ResponseEntity<? extends MusicResponse> delete(@PathVariable Long id) {
        try {
            var req = BandRequest.Delete.builder().id(id).build();
            return toResponse(producer.sendAndReceive(req).get(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return timeout("deleteBand(" + id + ")", e);
        } catch (Exception e) {
            return internal(e);
        }
    }
}