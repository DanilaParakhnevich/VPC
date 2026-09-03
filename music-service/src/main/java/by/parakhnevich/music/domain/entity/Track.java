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

    @Column(name = "trackPath")
    private String trackPath;

    @Column(name = "duration")
    private int duration;

    //  For position in album or single (happens that there are more than one track)
    @Column(name = "track_number")
    private Short trackNumber;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "track_album",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "album_id")
    )
    @ToString.Exclude
    private List<Album> albums;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "track_single",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "single_id")
    )
    @ToString.Exclude
    private List<Single> singles;

    @ManyToMany
    @JoinTable(
            name = "track_genre",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @ToString.Exclude
    private List<Genre> genres;

}
