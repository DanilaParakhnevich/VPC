package by.parakhnevich.music.repository;

import by.parakhnevich.music.domain.entity.Band;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

public class BandRepository implements PanacheRepository<Band> {

    public List<Band> findByMusician(Long musicianId) {
        return find("select c join c.musicians s where s.id = ?1", musicianId).list();
    }

    public List<Band> findByMusicianPageable(Long musicianId, int pageIndex, int pageSize) {
        return find("select c join c.musicians s where s.id = ?1", musicianId).page(pageIndex, pageSize).list();
    }

    public List<Band> findByGenre(Long genreId) {
        return find("select c join c.genres s where s.id = ?1", genreId).list();
    }

    public List<Band> findByGenrePageable(Long genreId, int pageIndex, int pageSize) {
        return find("select c join c.genres s where s.id = ?1", genreId).page(pageIndex, pageSize).list();
    }

}
