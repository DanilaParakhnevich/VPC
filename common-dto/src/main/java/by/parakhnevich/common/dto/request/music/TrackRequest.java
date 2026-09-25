package by.parakhnevich.common.dto.request.music;

import lombok.Builder;
import lombok.With;

import java.util.List;
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
            String title,
            String trackPathId,
            Integer duration,
            List<Long> bandIds,
            List<Long> albumIds,
            List<Long> genreIds,
            List<Long> collectionIds
    ) implements TrackRequest {
        public Create {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder
    @With
    record GetById(
            String requestId,
            Long id
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
            String titleLike,
            Long genreId,
            Long bandId,
            Long musicianId
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