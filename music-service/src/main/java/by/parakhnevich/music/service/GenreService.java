package by.parakhnevich.music.service;

import by.parakhnevich.common.dto.request.music.GenreRequest;
import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.music.domain.entity.Genre;
import by.parakhnevich.music.repository.GenreRepository;
import by.parakhnevich.music.service.exception.AlreadyExistsException;
import by.parakhnevich.music.service.exception.NotFoundException;
import by.parakhnevich.music.utils.mapper.GenreMapper;
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
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Transactional
    public MusicResponse.GenreSingle create(GenreRequest.Create req) {
        genreRepository.find("title", req.title()).firstResultOptional().ifPresent(g -> {
            throw new AlreadyExistsException("Genre '" + req.title() + "' already exists");
        });
        var genre = Genre.builder()
                .title(req.title())
                .description(req.description())
                .build();
        genreRepository.persist(genre);
        return genreMapper.toSingle(req.requestId(), genre);
    }

    public MusicResponse.GenreSingle getById(GenreRequest.GetById req) {
        var genre = genreRepository.findByIdOptional(req.id())
                .orElseThrow(() -> new NotFoundException("Genre " + req.id() + " not found"));
        return genreMapper.toSingle(req.requestId(), genre);
    }

    public MusicResponse.Page getAll(GenreRequest.GetAll req) {
        var jpql = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (req.titleLike() != null && !req.titleLike().isBlank()) {
            jpql.append(" and lower(title) like :titleLike");
            params.put("titleLike", "%" + req.titleLike().toLowerCase() + "%");
        }

        var query = genreRepository.find(jpql.toString(), params);
        query.page(Page.of(req.page(), req.size()));

        List<MusicResponse.Single> content = query.list().stream()
                .<MusicResponse.Single>map(g -> genreMapper.toSingle(req.requestId(), g))
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
    public MusicResponse.GenreSingle update(GenreRequest.Update req) {
        var genre = genreRepository.findByIdOptional(req.id())
                .orElseThrow(() -> new NotFoundException("Genre " + req.id() + " not found"));

        var u = req.updates();
        if (u.get("title")       instanceof String title) genre.setTitle(title);
        if (u.get("description") instanceof String desc)  genre.setDescription(desc);

        return genreMapper.toSingle(req.requestId(), genre);
    }

    @Transactional
    public MusicResponse.Deleted delete(GenreRequest.Delete req) {
        boolean removed = genreRepository.deleteById(req.id());
        if (!removed) throw new NotFoundException("Genre " + req.id() + " not found");
        return MusicResponse.Deleted.builder()
                .requestId(req.requestId())
                .entity("GENRE")
                .id(req.id())
                .build();
    }
}