package by.parakhnevich.music.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Created by agallochum on 2026-09-02
 */
@Entity
@Table(name = "bands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Band extends BaseEntity {

    private String name;

    private String description;

    private String geo;

    @ManyToMany
    @JoinTable(
            name = "band_genre",
            joinColumns = @JoinColumn(name = "band_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Musician> musicians;

    @ManyToMany
    @JoinTable(
            name = "band_genre",
            joinColumns = @JoinColumn(name = "band_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;
}
