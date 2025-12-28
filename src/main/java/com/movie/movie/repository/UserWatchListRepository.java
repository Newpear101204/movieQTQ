package com.movie.movie.repository;

import com.movie.movie.entity.Movies;
import com.movie.movie.entity.UserWatchList;
import com.movie.movie.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWatchListRepository extends JpaRepository<UserWatchList, Long> {
    UserWatchList findByUserAndMovie(Users user, Movies movie);
    boolean existsByUserAndMovie(Users user, Movies movie);
}
