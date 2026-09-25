package by.parakhnevich.music.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Created by agallochum on 2026-09-02
 */
@Entity
@Table(name = "tracks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Track extends BaseEntity {

    @Column(name = "title")
    private String title;

    @Column(name = "track_path_id")
    private String trackPathId;

    @Column(name = "duration")
    private int duration;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "track_band",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "band_id")
    )
    @ToString.Exclude
    private List<Band> bands;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "track_collection",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "collection_id")
    )
    @ToString.Exclude
    private List<Collection> collections;

    @ManyToMany
    @JoinTable(
            name = "track_genre",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @ToString.Exclude
    private List<Genre> genres;

}
