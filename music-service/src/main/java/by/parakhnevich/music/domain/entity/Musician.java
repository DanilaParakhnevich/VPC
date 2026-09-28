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
@ToString
public class Musician extends BaseEntity {

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @ManyToMany(mappedBy = "musicians", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Band> bands;
}