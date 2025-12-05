package com.movie.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "episodes",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_episode", columnNames = {"season_id", "episode_number"})
        },
        indexes = {
                @Index(name = "idx_season", columnList = "season_id"),
                @Index(name = "idx_slug", columnList = "slug")
        })
public class Episodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "episode_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", referencedColumnName = "season_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_episode_season"))
    Seasons season;

    @Column(name = "episode_number", nullable = false)
    Integer episodeNumber;

    @Column(name = "title", nullable = false, length = 255)
    String title;

    @Column(name = "slug", length = 500)
    String slug;

    @Column(name = "overview", columnDefinition = "TEXT")
    String overview;

    @Column(name = "thumbnail_url", length = 500)
    String thumbnailUrl;

    @Column(name = "video_url", length = 500)
    String videoUrl;

    @Column(name = "air_date")
    LocalDate airDate;

    @Column(name = "runtime")
    Integer runtime;

    @Column(name = "view_count")
    Long viewCount = 0L;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
