package by.parakhnevich.music.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "musicians")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Musician extends BaseEntity {

    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String bio;
}
