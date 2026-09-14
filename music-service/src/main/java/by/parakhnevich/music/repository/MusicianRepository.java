package by.parakhnevich.music.repository;

import by.parakhnevich.music.domain.entity.Musician;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

public class MusicianRepository implements PanacheRepository<Musician> {

    public List<Musician> findByBand(Long bandId) {
        return find("select c join c.bands s where s.id = ?1", bandId).list();
    }

    public List<Musician> findByBandPageable(Long bandId, int pageIndex, int pageSize) {
        return find("select c join c.bands s where s.id = ?1", bandId).page(pageIndex, pageSize).list();
    }

}
