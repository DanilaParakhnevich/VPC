package by.parakhnevich.common.dto.request.music;

import lombok.Builder;
import lombok.With;

import java.util.UUID;

/**
 * Created by agallochum on 2026-09-23
 */
public sealed interface TrackRequest extends MusicRequest permits
        TrackRequest.Create, TrackRequest.GetById, TrackRequest.GetAll,
        TrackRequest.Update, TrackRequest.Delete {

    @Builder
    @With
    record Create(
            String requestId,
            String name,
            String description,
            String geo
    ) implements TrackRequest {
        public Create {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder
    @With
    record GetById(
            String requestId,
            String name,
            String description,
            String geo
    ) implements TrackRequest {
        public GetById {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder
    @With
    record GetAll(
            String requestId,
            Integer page,
            Integer size,
            String nameLike,
            String geoLike
    ) implements TrackRequest {
        public GetAll {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder
    @With
    record Update(
            String requestId,
            Long id,
            java.util.Map<String, Object> updates
    ) implements TrackRequest {
        public Update {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder
    @With
    record Delete(
            String requestId,
            Long id
    ) implements TrackRequest {
        public Delete {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }
}