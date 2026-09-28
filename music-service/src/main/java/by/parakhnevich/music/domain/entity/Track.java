package by.parakhnevich.music.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "track_path_id", columnDefinition = "text", unique = true)
    private String trackPathId;

    @Column(name = "duration")
    private Integer duration;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "track_genre",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @ToString.Exclude
    private List<Genre> genres;
}