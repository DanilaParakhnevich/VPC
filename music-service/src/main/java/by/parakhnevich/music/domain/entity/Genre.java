package by.parakhnevich.music.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

/**
 * Created by agallochum on 2026-09-02
 */
@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Genre extends BaseEntity {

    private String title;

    private String description;

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "track_genre",
//            joinColumns = @JoinColumn(name = "genre_id"),
//            inverseJoinColumns = @JoinColumn(name = "track_id")
//    )
//    @ToString.Exclude
//    private List<Track> tracks;

}
