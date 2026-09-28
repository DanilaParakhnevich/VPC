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
@Table(name = "bands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Band extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 40)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "geo", length = 40)
    private String geo;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "band_musician",
            joinColumns = @JoinColumn(name = "band_id"),
            inverseJoinColumns = @JoinColumn(name = "musician_id")
    )
    @ToString.Exclude
    private List<Musician> musicians;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "band_genre",
            joinColumns = @JoinColumn(name = "band_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @ToString.Exclude
    private List<Genre> genres;

    @ManyToMany(mappedBy = "bands", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Track> tracks;
}