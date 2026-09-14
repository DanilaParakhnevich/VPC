package by.parakhnevich.music.repository;

import by.parakhnevich.music.domain.entity.Track;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

public class TrackRepository implements PanacheRepository<Track> {

    public List<Track> findByBand(Long bandId) {
        return find("select c join c.bands s where s.id = ?1", bandId).list();
    }

    public List<Track> findByBandPageable(Long bandId, int pageIndex, int pageSize) {
        return find("select c join c.bands s where s.id = ?1", bandId).page(pageIndex, pageSize).list();
    }

    public List<Track> findByCollection(Long collectionId) {
        return find("select c join c.collections s where s.id = ?1", collectionId).list();
    }

    public List<Track> findByCollectionPageable(Long collectionId, int pageIndex, int pageSize) {
        return find("select c join c.collections s where s.id = ?1", collectionId).page(pageIndex, pageSize).list();
    }

    public List<Track> findByGenre(Long genreId) {
        return find("select c join c.genres s where s.id = ?1", genreId).list();
    }

    public List<Track> findByGenrePageable(Long genreId, int pageIndex, int pageSize) {
        return find("select c join c.genres s where s.id = ?1", genreId).page(pageIndex, pageSize).list();
    }
}
