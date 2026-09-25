package by.parakhnevich.music.utils.mapper;

import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.music.domain.entity.Collection;
import by.parakhnevich.music.domain.entity.Track;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CollectionMapper {

    @Mapping(target = "collectionType", expression = "java(collection.getCollectionType() == null ? null : collection.getCollectionType().name())")
    @Mapping(target = "trackIds",       expression = "java(toTrackIds(collection.getSingle()))")
    MusicResponse.CollectionSingle toSingle(Collection collection);

    default MusicResponse.CollectionSingle toSingle(String requestId, Collection collection) {
        if (collection == null) return null;
        return toSingle(collection).withRequestId(requestId);
    }

    default List<Long> toTrackIds(List<Track> tracks) {
        return tracks == null ? List.of() : tracks.stream().map(Track::getId).toList();
    }
}