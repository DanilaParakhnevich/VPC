package by.parakhnevich.common.dto.request.music;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.With;

import java.util.List;
import java.util.UUID;

/**
 * Created by agallochum on 2026-09-23
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "action", visible = false)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BandRequest.Create.class,  name = "CREATE"),
        @JsonSubTypes.Type(value = BandRequest.GetById.class, name = "GET_BY_ID"),
        @JsonSubTypes.Type(value = BandRequest.GetAll.class,  name = "GET_ALL"),
        @JsonSubTypes.Type(value = BandRequest.Update.class,  name = "UPDATE"),
        @JsonSubTypes.Type(value = BandRequest.Delete.class,  name = "DELETE"),
})
public sealed interface BandRequest extends MusicRequest permits
        BandRequest.Create, BandRequest.GetById, BandRequest.GetAll,
        BandRequest.Update, BandRequest.Delete {

    @Builder @With
    record Create(
            String requestId,
            String imageId,
            String name,
            String description,
            String geo,
            List<Integer> genres
            ) implements BandRequest {
        public Create {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder @With
    record GetById(
            String requestId,
            Long id
    ) implements BandRequest {
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
            String geo
    ) implements BandRequest {
        public GetAll {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder @With
    record Update(
            String requestId,
            Long id,
            java.util.Map<String, Object> updates
    ) implements BandRequest {
        public Update {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

    @Builder @With
    record Delete(
            String requestId,
            Long id
    ) implements BandRequest {
        public Delete {
            if (requestId == null) requestId = UUID.randomUUID().toString();
        }
    }

}
