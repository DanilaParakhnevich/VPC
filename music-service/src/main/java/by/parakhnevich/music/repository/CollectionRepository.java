package by.parakhnevich.music.repository;

import by.parakhnevich.music.domain.entity.Collection;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

public class CollectionRepository implements PanacheRepository<Collection> {

    public List<Collection> findByTrack(Long trackId) {
        return find("select c join c.tracks s where s.id = ?1", trackId).list();
    }

    public List<Collection> findByTrackPageable(Long trackId, int pageIndex, int pageSize) {
        return find("select c join c.tracks s where s.id = ?1", trackId).page(pageIndex, pageSize).list();
    }

}
