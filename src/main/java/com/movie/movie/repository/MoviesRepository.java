package com.movie.movie.repository;

import com.movie.movie.entity.Movies;
import com.movie.movie.entity.Persons;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoviesRepository extends JpaRepository <Movies, Long> {
    List<Movies> findByTitleContaining(String keyword);

}
