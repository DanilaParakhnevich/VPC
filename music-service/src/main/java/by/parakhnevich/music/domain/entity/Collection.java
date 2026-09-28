package by.parakhnevich.music.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "collections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Collection extends BaseEntity {

    @Column(name = "title", nullable = false, length = 40)
    private String title;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "collection_type", length = 40)
    private CollectionType collectionType;

    @ManyToMany(mappedBy = "collections", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Track> tracks;

    public enum CollectionType {
        ALBUM,
        SINGLE_OR_EP,
        DEMO_ALBUM
    }
}