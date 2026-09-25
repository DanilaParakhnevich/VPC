package by.parakhnevich.music.utils.mapper;

import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.music.domain.entity.Band;
import by.parakhnevich.music.domain.entity.Collection;
import by.parakhnevich.music.domain.entity.Genre;
import by.parakhnevich.music.domain.entity.Track;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrackMapper {

    @Mapping(target = "bandIds",       expression = "java(toBandIds(track.getBands()))")
    @Mapping(target = "collectionIds", expression = "java(toCollectionIds(track.getCollections()))")
    @Mapping(target = "genreIds",      expression = "java(toGenreIds(track.getGenres()))")
    MusicResponse.TrackSingle toSingle(Track track);

    default MusicResponse.TrackSingle toSingle(String requestId, Track track) {
        if (track == null) return null;
        return toSingle(track).withRequestId(requestId);
    }

    default List<Long> toBandIds(List<Band> bands) {
        return bands == null ? List.of() : bands.stream().map(Band::getId).toList();
    }
    default List<Long> toCollectionIds(List<Collection> collections) {
        return collections == null ? List.of() : collections.stream().map(Collection::getId).toList();
    }
    default List<Long> toGenreIds(List<Genre> genres) {
        return genres == null ? List.of() : genres.stream().map(Genre::getId).toList();
    }
}