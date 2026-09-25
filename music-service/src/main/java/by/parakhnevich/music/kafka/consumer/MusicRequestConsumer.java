package by.parakhnevich.music.kafka.consumer;

import by.parakhnevich.common.dto.request.music.*;
import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.music.service.*;
import by.parakhnevich.music.utils.CustomObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

/**
 * Created by agallochum on 2026-05-18
 */
@ApplicationScoped
@RequiredArgsConstructor
public class MusicRequestConsumer {

    private static final Logger LOGGER = LogManager.getLogger(MusicRequestConsumer.class);

    @Inject
    @Channel("users-response")
    Emitter<String> emitter;
    @Inject
    BandService bandService;
    @Inject
    CollectionService collectionService;
    @Inject
    GenreService genreService;
    @Inject
    MusicianService musicianService;
    @Inject
    TrackService trackService;
    @Inject
    CustomObjectMapper objectMapper;

    @Incoming("users-request")
    @Transactional
    public void consume(String userRequestStr) {
        MusicRequest musicRequest = null;
        try {
            musicRequest = objectMapper.readValue(userRequestStr, MusicRequest.class);

            MusicResponse response = switch (musicRequest) {
                case BandRequest b      -> switch (b) {
                    case BandRequest.Create create -> bandService.create(create);
                    case BandRequest.Delete delete -> bandService.delete(delete);
                    case BandRequest.GetAll getAll -> bandService.getAll(getAll);
                    case BandRequest.GetById getById -> bandService.getById(getById);
                    case BandRequest.Update update -> bandService.update(update);
                };
                case CollectionRequest c  -> switch (c) {
                    case CollectionRequest.Create create -> collectionService.create(create);
                    case CollectionRequest.Delete delete -> collectionService.delete(delete);
                    case CollectionRequest.GetAll getAll -> collectionService.getAll(getAll);
                    case CollectionRequest.GetById getById -> collectionService.getById(getById);
                    case CollectionRequest.Update update -> collectionService.update(update);
                };
                case GenreRequest g       -> switch (g) {
                    case GenreRequest.Create create -> genreService.create(create);
                    case GenreRequest.Delete delete -> genreService.delete(delete);
                    case GenreRequest.GetAll getAll -> genreService.getAll(getAll);
                    case GenreRequest.GetById getById -> genreService.getById(getById);
                    case GenreRequest.Update update -> genreService.update(update);
                };
                case MusicianRequest m -> switch (m) {
                    case MusicianRequest.Create create -> musicianService.create(create);
                    case MusicianRequest.Delete delete -> musicianService.delete(delete);
                    case MusicianRequest.GetAll getAll -> musicianService.getAll(getAll);
                    case MusicianRequest.GetById getById -> musicianService.getById(getById);
                    case MusicianRequest.Update update -> musicianService.update(update);
                };
                case TrackRequest t    -> switch (t) {
                    case TrackRequest.Create create -> trackService.create(create);
                    case TrackRequest.Delete delete -> trackService.delete(delete);
                    case TrackRequest.GetAll getAll -> trackService.getAll(getAll);
                    case TrackRequest.GetById getById -> trackService.getById(getById);
                    case TrackRequest.Update update -> trackService.update(update);
                };
            };

            LOGGER.info("Sending response for request: {}", musicRequest.requestId());
            emitter.send(objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to parse music request", e);
            sendError(MusicResponse.ResponseCode.BAD_REQUEST, musicRequest);
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            sendError(MusicResponse.ResponseCode.BAD_REQUEST, musicRequest);
        }
    }

    private void sendError(MusicResponse.ResponseCode responseCode, MusicRequest request) {
        if (request == null) {
            LOGGER.warn("Cannot send error {} - request not parsed", responseCode);
            return;
        }
        try {
            emitter.send(objectMapper.writeValueAsString(
                    MusicResponse.ErrorResponse.builder()
                            .requestId(request.requestId())
                            .responseCode(responseCode)
                            .build()));
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize error response", e);
        }
    }
}