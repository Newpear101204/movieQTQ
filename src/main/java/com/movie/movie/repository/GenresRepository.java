package com.movie.movie.repository;

import com.movie.movie.entity.Genres;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenresRepository extends JpaRepository<Genres, Integer> {
    Genres findByGenreName(String name);

    Genres findByGenreSlug(String genreSlug);
}
