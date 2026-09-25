package by.parakhnevich.music.utils.mapper;

import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.music.domain.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GenreMapper {

    MusicResponse.GenreSingle toSingle(Genre genre);

    default MusicResponse.GenreSingle toSingle(String requestId, Genre genre) {
        if (genre == null) return null;
        return toSingle(genre).withRequestId(requestId);
    }
}