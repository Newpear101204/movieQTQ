package com.movie.movie.repository;

import com.movie.movie.entity.Movies;
import com.movie.movie.entity.Users;
import com.movie.movie.entity.WatchHistory;
import com.movie.movie.model.response.MovieResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    // Tìm xem phim này đã có trong lịch sử chưa
    Optional<WatchHistory> findByUserAndMovie(Users user, Movies movie);

    // Lấy danh sách lịch sử, phim mới xem hiện lên đầu
    List<WatchHistory> findByUserOrderByLastWatchedAtDesc(Users user);
}
