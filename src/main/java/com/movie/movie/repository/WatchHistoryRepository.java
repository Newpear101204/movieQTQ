package com.movie.movie.repository;

import com.movie.movie.entity.WatchHistory;
import com.movie.movie.model.response.MovieResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
}
