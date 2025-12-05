package com.movie.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "seasons",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_season", columnNames = {"movie_id", "season_number"})
        },
        indexes = {
                @Index(name = "idx_movie", columnList = "movie_id")
        })
public class Seasons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "season_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_season_movie"))
    Movies movie;

    @Column(name = "season_number", nullable = false)
    Integer seasonNumber;

    @Column(name = "title", length = 255)
    String title;

    @Column(name = "overview", columnDefinition = "TEXT")
    String overview;

    @Column(name = "poster_url", length = 500)
    String posterUrl;

    @Column(name = "air_date")
    LocalDate airDate;

    @Column(name = "episode_count")
    Integer episodeCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Episodes> episodes;

}
