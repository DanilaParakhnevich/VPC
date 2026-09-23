package by.parakhnevich.music.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by agallochum on 2026-09-02
 */
@Entity
@Table(name = "musicians")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Musician extends BaseEntity {

    private String name;

    private String imageId;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "band_musician",
            joinColumns = @JoinColumn(name = "musician_id"),
            inverseJoinColumns = @JoinColumn(name = "band_id")
    )
    private List<Band> bands;
}
