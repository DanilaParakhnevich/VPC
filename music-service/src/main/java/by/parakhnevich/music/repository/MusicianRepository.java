package by.parakhnevich.music.repository;

import by.parakhnevich.music.domain.entity.Musician;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Created by agallochum on 2026-09-25
 */
@ApplicationScoped
public class MusicianRepository implements PanacheRepository<Musician> {

    public List<Musician> findByBand(Long bandId) {
        return find("select c join c.bands s where s.id = ?1", bandId).list();
    }

    public List<Musician> findByBandPageable(Long bandId, int pageIndex, int pageSize) {
        return find("select c join c.bands s where s.id = ?1", bandId).page(pageIndex, pageSize).list();
    }

}
