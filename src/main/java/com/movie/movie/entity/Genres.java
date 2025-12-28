package com.movie.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "genres", indexes = {
        @Index(name = "idx_slug", columnList = "genre_slug")
})
public class Genres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    Integer id;

    @Column(name = "genre_name", nullable = false, unique = true, length = 100)
    String genreName;

    @Column(name = "genre_slug", nullable = false, unique = true, length = 100)
    String genreSlug;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL)
    List<MovieGenres> movieGenres;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;
}
