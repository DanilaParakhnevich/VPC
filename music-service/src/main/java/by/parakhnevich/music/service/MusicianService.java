package by.parakhnevich.music.service;

import by.parakhnevich.common.dto.request.music.MusicianRequest;
import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.music.domain.entity.Musician;
import by.parakhnevich.music.repository.MusicianRepository;
import by.parakhnevich.music.service.exception.NotFoundException;
import by.parakhnevich.music.utils.mapper.MusicianMapper;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by agallochum on 2026-09-24
 */
@ApplicationScoped
@RequiredArgsConstructor
public class MusicianService {

    private final MusicianRepository musicianRepository;
    private final MusicianMapper musicianMapper;

    @Transactional
    public MusicResponse.MusicianSingle create(MusicianRequest.Create req) {
        var musician = Musician.builder()
                .name(req.name())
                .imageId(req.imageId())
                .birthDate(req.birthDate())
                .build();
        musicianRepository.persist(musician);
        return musicianMapper.toSingle(req.requestId(), musician);
    }

    public MusicResponse.MusicianSingle getById(MusicianRequest.GetById req) {
        var musician = musicianRepository.findByIdOptional(req.id())
                .orElseThrow(() -> new NotFoundException("Musician " + req.id() + " not found"));
        return musicianMapper.toSingle(req.requestId(), musician);
    }

    public MusicResponse.Page getAll(MusicianRequest.GetAll req) {
        var jpql = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (req.nameLike() != null && !req.nameLike().isBlank()) {
            jpql.append(" and lower(name) like :titleLike");
            params.put("titleLike", "%" + req.nameLike().toLowerCase() + "%");
        }

        var query = musicianRepository.find(jpql.toString(), params);
        query.page(Page.of(req.page(), req.size()));

        List<MusicResponse.Single> content = query.list().stream()
                .<MusicResponse.Single>map(m -> musicianMapper.toSingle(req.requestId(), m))
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
    public MusicResponse.MusicianSingle update(MusicianRequest.Update req) {
        var musician = musicianRepository.findByIdOptional(req.id())
                .orElseThrow(() -> new NotFoundException("Musician " + req.id() + " not found"));

        var u = req.updates();
        if (u.get("name")      instanceof String name)  musician.setName(name);
        if (u.get("imageId")   instanceof String img)   musician.setImageId(img);
        if (u.get("birthDate") instanceof String date)  musician.setBirthDate(LocalDate.parse(date));

        return musicianMapper.toSingle(req.requestId(), musician);
    }

    @Transactional
    public MusicResponse.Deleted delete(MusicianRequest.Delete req) {
        boolean removed = musicianRepository.deleteById(req.id());
        if (!removed) throw new NotFoundException("Musician " + req.id() + " not found");
        return MusicResponse.Deleted.builder()
                .requestId(req.requestId())
                .entity("MUSICIAN")
                .id(req.id())
                .build();
    }
}