package by.parakhnevich.music.utils.mapper;

import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.music.domain.entity.Band;
import by.parakhnevich.music.domain.entity.Musician;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MusicianMapper {

    @Mapping(target = "bandIds", expression = "java(toBandIds(musician.getBands()))")
    MusicResponse.MusicianSingle toSingle(Musician musician);

    default MusicResponse.MusicianSingle toSingle(String requestId, Musician musician) {
        if (musician == null) return null;
        return toSingle(musician).withRequestId(requestId);
    }

    default List<Long> toBandIds(List<Band> bands) {
        return bands == null ? List.of() : bands.stream().map(Band::getId).toList();
    }
}