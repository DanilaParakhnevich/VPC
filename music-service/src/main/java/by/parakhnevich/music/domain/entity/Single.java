package by.parakhnevich.music.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "singles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Single extends BaseEntity {

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "track_single",
            joinColumns = @JoinColumn(name = "single_id"),
            inverseJoinColumns = @JoinColumn(name = "track_id")
    )
    @ToString.Exclude
    private List<Track> single;

}
