package com.movie.movie.repository;

import com.movie.movie.entity.Episodes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodeRepsitory extends JpaRepository<Episodes,Long> {
}
