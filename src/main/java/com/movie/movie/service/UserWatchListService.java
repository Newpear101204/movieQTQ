package com.movie.movie.service;

public interface UserWatchListService {
    void addToWatchList (Long id );

    void removeFromWatchList(Long movieId);
}
