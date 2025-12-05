package com.movie.movie.repository;

import com.movie.movie.entity.UserWatchList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWatchListRepository extends JpaRepository<UserWatchList, Long> {
}
