package by.parakhnevich.common.dto.request.music;

import lombok.Builder;
import lombok.With;

import java.util.UUID;

/**
 * Created by agallochum on 2026-09-23
 */
public sealed interface GenreRequest extends MusicRequest permits
        GenreRequest.Create, GenreRequest.GetById, GenreRequest.GetAll,
        GenreRequest.Update, GenreRequest.Delete {

    @Builder @With
    record Create(
            String requestId,
            String title,
            String description
    ) implements GenreRequest {
        public Create {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder @With
    record GetById(
            String requestId,
            Long id
    ) implements GenreRequest {
        public GetById {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder @With
    record GetAll(
            String requestId,
            Integer page,
            Integer size,
            String titleLike
    ) implements GenreRequest {
        public GetAll {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder @With
    record Update(
            String requestId,
            Long id,
            java.util.Map<String, Object> updates
    ) implements GenreRequest {
        public Update {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder @With
    record Delete(
            String requestId,
            Long id
    ) implements GenreRequest {
        public Delete {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

}
