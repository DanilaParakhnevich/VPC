package by.parakhnevich.music.service;

import by.parakhnevich.common.dto.request.music.TrackRequest;
import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.music.domain.entity.Track;
import by.parakhnevich.music.repository.BandRepository;
import by.parakhnevich.music.repository.CollectionRepository;
import by.parakhnevich.music.repository.GenreRepository;
import by.parakhnevich.music.repository.TrackRepository;
import by.parakhnevich.music.service.exception.NotFoundException;
import by.parakhnevich.music.utils.mapper.TrackMapper;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by agallochum on 2026-09-24
 */
@ApplicationScoped
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;
    private final BandRepository bandRepository;
    private final GenreRepository genreRepository;
    private final CollectionRepository collectionRepository;
    private final TrackMapper trackMapper;

    @Transactional
    public MusicResponse.TrackSingle create(TrackRequest.Create req) {
        var track = Track.builder()
                .title(req.title())
                .trackPathId(req.trackPathId())
                .duration(req.duration())
                .bands(req.bandIds() == null ? List.of() : bandRepository.list("id in ?1", req.bandIds()))
                .genres(req.genreIds() == null ? List.of() : genreRepository.list("id in ?1", req.genreIds()))
                .collections(req.collectionIds() == null ? List.of() : collectionRepository.list("id in ?1", req.collectionIds()))
                .build();
        trackRepository.persist(track);
        return trackMapper.toSingle(req.requestId(), track);
    }

    public MusicResponse.TrackSingle getById(TrackRequest.GetById req) {
        var track = trackRepository.findByIdOptional(req.id())
                .orElseThrow(() -> new NotFoundException("Track " + req.id() + " not found"));
        return trackMapper.toSingle(req.requestId(), track);
    }

    public MusicResponse.Page getAll(TrackRequest.GetAll req) {
        var jpql = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (req.titleLike() != null && !req.titleLike().isBlank()) {
            jpql.append(" and lower(title) like :titleLike");
            params.put("titleLike", "%" + req.titleLike().toLowerCase() + "%");
        }
        if (req.bandId() != null) {
            jpql.append(" and :bandId in (select b.id from Band b join b.tracks t where t.id = id)");
            // проще: отдельный join. ќставл€ю примерный вариант Ч поправь под свои св€зи.
            params.put("bandId", req.bandId());
        }
        if (req.genreId() != null) {
            jpql.append(" and :genreId in (select g.id from Genre g join g.tracks t where t.id = id)");
            params.put("genreId", req.genreId());
        }

        var query = trackRepository.find(jpql.toString(), params);
        query.page(Page.of(req.page(), req.size()));

        List<MusicResponse.Single> content = query.list().stream()
                .<MusicResponse.Single>map(t -> trackMapper.toSingle(req.requestId(), t))
                .toList();

        return MusicResponse.Page.builder()
                .requestId(req.requestId())
                .content(content)
                .page(req.page())
                .size(req.size())
                .totalElements(query.count())
                .totalPages(query.pageCount())
                .build();
    }

    @Transactional
    public MusicResponse.TrackSingle update(TrackRequest.Update req) {
        var track = trackRepository.findByIdOptional(req.id())
                .orElseThrow(() -> new NotFoundException("Track " + req.id() + " not found"));

        var u = req.updates();
        if (u.get("title")       instanceof String title)   track.setTitle(title);
        if (u.get("trackPathId") instanceof String path)    track.setTrackPathId(path);
        if (u.get("duration")    instanceof Number dur)     track.setDuration(dur.intValue());
        if (u.get("bandIds")     instanceof List<?> bandIds)
            track.setBands(bandRepository.list("id in ?1", bandIds));
        if (u.get("genreIds")    instanceof List<?> genreIds)
            track.setGenres(genreRepository.list("id in ?1", genreIds));
        if (u.get("collectionIds") instanceof List<?> collectionIds)
            track.setCollections(collectionRepository.list("id in ?1", collectionIds));

        return trackMapper.toSingle(req.requestId(), track);
    }

    @Transactional
    public MusicResponse.Deleted delete(TrackRequest.Delete req) {
        boolean removed = trackRepository.deleteById(req.id());
        if (!removed) throw new NotFoundException("Track " + req.id() + " not found");
        return MusicResponse.Deleted.builder()
                .requestId(req.requestId())
                .entity("TRACK")
                .id(req.id())
                .build();
    }
}