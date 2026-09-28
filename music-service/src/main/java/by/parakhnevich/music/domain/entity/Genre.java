package by.parakhnevich.music.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Genre extends BaseEntity {

    @Column(name = "title", nullable = false, unique = true, length = 40)
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Track> tracks;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Band> bands;
}