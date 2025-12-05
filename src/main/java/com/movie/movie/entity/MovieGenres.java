package com.movie.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "movie_genres")
public class MovieGenres {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id", foreignKey = @ForeignKey(name = "fk_movie_genres_movie"), nullable = false)
    Movies movie;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", referencedColumnName = "genre_id", foreignKey = @ForeignKey(name = "fk_movie_genres_genre"), nullable = false)
    Genres genre;
}
