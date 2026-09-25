package by.parakhnevich.music.service;

import by.parakhnevich.common.dto.request.music.BandRequest;
import by.parakhnevich.common.dto.response.music.MusicResponse;
import by.parakhnevich.music.domain.entity.Band;
import by.parakhnevich.music.repository.BandRepository;
import by.parakhnevich.music.service.exception.AlreadyExistsException;
import by.parakhnevich.music.service.exception.NotFoundException;
import by.parakhnevich.music.utils.mapper.BandMapper;
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
public class BandService {

    private final BandRepository bandRepository;
    private final BandMapper bandMapper;

    @Transactional
    public MusicResponse.BandSingle create(BandRequest.Create req) {
        bandRepository.find("name", req.name()).firstResultOptional().ifPresent(b -> {
            throw new AlreadyExistsException("Band with name '" + req.name() + "' already exists");
        });

        var band = Band.builder()
                .name(req.name())
                .description(req.description())
                .geo(req.geo())
                .imageId(req.imageId())
                .build();
        bandRepository.persist(band);
        return bandMapper.toSingle(req.requestId(), band);
    }

    public MusicResponse.BandSingle getById(BandRequest.GetById req) {
        var band = bandRepository.findByIdOptional(req.id())
                .orElseThrow(() -> new NotFoundException("Band " + req.id() + " not found"));
        return bandMapper.toSingle(req.requestId(), band);
    }

    public MusicResponse.Page getAll(BandRequest.GetAll req) {
        var jpql = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (req.nameLike() != null && !req.nameLike().isBlank()) {
            jpql.append(" and lower(name) like :titleLike");
            params.put("titleLike", "%" + req.nameLike().toLowerCase() + "%");
        }
        if (req.geo() != null && !req.geo().isBlank()) {
            jpql.append(" and geo = :geo");
            params.put("geo", req.geo());
        }

        var query = bandRepository.find(jpql.toString(), params);
        query.page(Page.of(req.page(), req.size()));

        List<MusicResponse.Single> content = query.list().stream()
                .<MusicResponse.Single>map(b -> bandMapper.toSingle(req.requestId(), b))
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
    public MusicResponse.BandSingle update(BandRequest.Update req) {
        var band = bandRepository.findByIdOptional(req.id())
                .orElseThrow(() -> new NotFoundException("Band " + req.id() + " not found"));

        var u = req.updates();
        if (u.get("name") instanceof String name && !name.isBlank()) {
            if (!name.equals(band.getName())) {
                bandRepository.find("name", name).firstResultOptional().ifPresent(b -> {
                    throw new AlreadyExistsException("Band with name '" + name + "' already exists");
                });
            }
            band.setName(name);
        }
        if (u.get("description") instanceof String desc) band.setDescription(desc);
        if (u.get("geo")         instanceof String geo)  band.setGeo(geo);
        if (u.get("imageId")     instanceof String img)  band.setImageId(img);

        return bandMapper.toSingle(req.requestId(), band);
    }

    @Transactional
    public MusicResponse.Deleted delete(BandRequest.Delete req) {
        boolean removed = bandRepository.deleteById(req.id());
        if (!removed) throw new NotFoundException("Band " + req.id() + " not found");
        return MusicResponse.Deleted.builder()
                .requestId(req.requestId())
                .entity("BAND")
                .id(req.id())
                .build();
    }
}