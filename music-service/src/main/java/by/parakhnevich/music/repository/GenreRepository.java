package by.parakhnevich.music.repository;

import by.parakhnevich.music.domain.entity.Genre;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

public class GenreRepository implements PanacheRepository<Genre> {

    public List<Genre> findByTrack(Long genreId) {
        return find("select c join c.tracks s where s.id = ?1", genreId).list();
    }

    public List<Genre> findByTrackPageable(Long trackId, int pageIndex, int pageSize) {
        return find("select c join c.tracks s where s.id = ?1", trackId).page(pageIndex, pageSize).list();
    }

}
