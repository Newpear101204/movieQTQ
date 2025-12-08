package com.movie.movie.service.impl;

import com.movie.movie.entity.Movies;
import com.movie.movie.entity.Users;
import com.movie.movie.entity.WatchHistory;
import com.movie.movie.repository.MoviesRepository;
import com.movie.movie.repository.UsersRepository;
import com.movie.movie.repository.WatchHistoryRepository;
import com.movie.movie.service.WatchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class WatchHistoryServiceImpl implements WatchHistoryService {
    @Autowired
    private WatchHistoryRepository watchHistoryRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MoviesRepository moviesRepository;


    @Override
    public void addToHistory(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepository.findByUsername(username);
        Movies movies = moviesRepository.findById(id).get();
        WatchHistory watchHistory = new WatchHistory();
        watchHistory.setUser(users);
        watchHistory.setMovie(movies);
        watchHistoryRepository.save(watchHistory);
    }
}
