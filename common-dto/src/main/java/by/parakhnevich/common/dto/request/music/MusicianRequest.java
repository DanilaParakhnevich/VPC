package by.parakhnevich.common.dto.request.music;

import lombok.Builder;
import lombok.With;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Created by agallochum on 2026-09-23
 */
public sealed interface MusicianRequest extends MusicRequest permits
        MusicianRequest.Create, MusicianRequest.GetById, MusicianRequest.GetAll,
        MusicianRequest.Update, MusicianRequest.Delete {

    @Builder @With
    record Create(
            String requestId,
            String name,
            String imageId,
            LocalDate birthDate
    ) implements MusicianRequest {
        public Create {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder @With
    record GetById(
            String requestId,
            Long id
    ) implements MusicianRequest {
        public GetById {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder @With
    record GetAll(
            String requestId,
            Integer page,
            Integer size,
            String nameLike,
            Long bandId
    ) implements MusicianRequest {
        public GetAll {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder @With
    record Update(
            String requestId,
            Long id,
            java.util.Map<String, Object> updates
    ) implements MusicianRequest {
        public Update {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder @With
    record Delete(
            String requestId,
            Long id
    ) implements MusicianRequest {
        public Delete {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

}
