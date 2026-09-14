package by.parakhnevich.music.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Created by agallochum on 2026-09-02
 */
@Entity
@Table(name = "collections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Collection extends BaseEntity {

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "collectionType")
    private CollectionType collectionType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "track_collection",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "track_id")
    )
    @ToString.Exclude
    private List<Track> single;

    public enum CollectionType {
        ALBUM,
        SINGLE_OR_EP,
        DEMO_ALBUM
    }
}
