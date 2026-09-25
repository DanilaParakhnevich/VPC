package by.parakhnevich.music.service;

import by.parakhnevich.common.dto.request.music.CollectionRequest;
import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.music.domain.entity.Collection;
import by.parakhnevich.music.repository.CollectionRepository;
import by.parakhnevich.music.repository.TrackRepository;
import by.parakhnevich.music.service.exception.BadRequestException;
import by.parakhnevich.music.service.exception.NotFoundException;
import by.parakhnevich.music.utils.mapper.CollectionMapper;
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
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final TrackRepository trackRepository;
    private final CollectionMapper collectionMapper;

    @Transactional
    public MusicResponse.CollectionSingle create(CollectionRequest.Create req) {
        Collection.CollectionType type;
        try {
            type = Collection.CollectionType.valueOf(req.collectionType());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown collection type: " + req.collectionType());
        }

        var collection = Collection.builder()
                .title(req.title())
                .description(req.description())
                .imageId(req.imageId())
                .collectionType(type)
                .single(req.trackIds() == null ? List.of() : trackRepository.list("id in ?1", req.trackIds()))
                .build();
        collectionRepository.persist(collection);
        return collectionMapper.toSingle(req.requestId(), collection);
    }

    public MusicResponse.CollectionSingle getById(CollectionRequest.GetById req) {
        var collection = collectionRepository.findByIdOptional(req.id())
                .orElseThrow(() -> new NotFoundException("Collection " + req.id() + " not found"));
        return collectionMapper.toSingle(req.requestId(), collection);
    }

    public MusicResponse.Page getAll(CollectionRequest.GetAll req) {
        var jpql = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (req.titleLike() != null && !req.titleLike().isBlank()) {
            jpql.append(" and lower(title) like :titleLike");
            params.put("titleLike", "%" + req.titleLike().toLowerCase() + "%");
        }
        if (req.collectionType() != null && !req.collectionType().isBlank()) {
            jpql.append(" and collectionType = :type");
            params.put("type", Collection.CollectionType.valueOf(req.collectionType()));
        }

        var query = collectionRepository.find(jpql.toString(), params);
        query.page(Page.of(req.page(), req.size()));

        List<MusicResponse.Single> content = query.list().stream()
                .<MusicResponse.Single>map(c -> collectionMapper.toSingle(req.requestId(), c))
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
    public MusicResponse.CollectionSingle update(CollectionRequest.Update req) {
        var collection = collectionRepository.findByIdOptional(req.id())
                .orElseThrow(() -> new NotFoundException("Collection " + req.id() + " not found"));

        var u = req.updates();
        if (u.get("title")       instanceof String title) collection.setTitle(title);
        if (u.get("description") instanceof String desc)  collection.setDescription(desc);
        if (u.get("imageId")     instanceof String img)   collection.setImageId(img);
        if (u.get("collectionType") instanceof String type) {
            collection.setCollectionType(Collection.CollectionType.valueOf(type));
        }
        if (u.get("trackIds") instanceof List<?> trackIds) {
            collection.setSingle(trackRepository.list("id in ?1", trackIds));
        }

        return collectionMapper.toSingle(req.requestId(), collection);
    }

    @Transactional
    public MusicResponse.Deleted delete(CollectionRequest.Delete req) {
        boolean removed = collectionRepository.deleteById(req.id());
        if (!removed) throw new NotFoundException("Collection " + req.id() + " not found");
        return MusicResponse.Deleted.builder()
                .requestId(req.requestId())
                .entity("COLLECTION")
                .id(req.id())
                .build();
    }
}