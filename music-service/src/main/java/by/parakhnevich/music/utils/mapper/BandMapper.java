package by.parakhnevich.music.utils.mapper;

import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.music.domain.entity.Band;
import by.parakhnevich.music.domain.entity.Genre;
import by.parakhnevich.music.domain.entity.Musician;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BandMapper {

    @Mapping(target = "musicianIds", expression = "java(toMusicianIds(band.getMusicians()))")
    @Mapping(target = "genreIds",    expression = "java(toGenreIds(band.getGenres()))")
    MusicResponse.BandSingle toSingle(Band band);

    default MusicResponse.BandSingle toSingle(String requestId, Band band) {
        if (band == null) return null;
        return toSingle(band).withRequestId(requestId);
    }

    default List<Long> toMusicianIds(List<Musician> musicians) {
        return musicians == null ? List.of()
                : musicians.stream().map(Musician::getId).toList();
    }

    default List<Long> toGenreIds(List<Genre> genres) {
        return genres == null ? List.of()
                : genres.stream().map(Genre::getId).toList();
    }
}