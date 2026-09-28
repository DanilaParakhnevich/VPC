package by.parakhnevich.music.repository;

import by.parakhnevich.music.domain.entity.Genre;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Created by agallochum on 2026-09-25
 */
@ApplicationScoped
public class GenreRepository implements PanacheRepository<Genre> {

}
