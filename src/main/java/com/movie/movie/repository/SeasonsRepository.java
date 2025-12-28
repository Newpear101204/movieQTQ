package com.movie.movie.repository;

import com.movie.movie.entity.Movies;
import com.movie.movie.entity.Seasons;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeasonsRepository extends JpaRepository<Seasons, Integer> {
    Seasons findByMovie(Movies movie);
}
