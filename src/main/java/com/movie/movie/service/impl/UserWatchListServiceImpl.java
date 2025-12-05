package com.movie.movie.service.impl;

import com.movie.movie.entity.Movies;
import com.movie.movie.entity.UserWatchList;
import com.movie.movie.entity.Users;
import com.movie.movie.repository.MoviesRepository;
import com.movie.movie.repository.UserWatchListRepository;
import com.movie.movie.repository.UsersRepository;
import com.movie.movie.service.UserWatchListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserWatchListServiceImpl implements UserWatchListService {

    @Autowired
    private UserWatchListRepository userWatchListRepository;

    @Autowired
    private MoviesRepository moviesRepository;

    @Autowired
    private UsersRepository  usersRepository;

    @Override
    public void addToWatchList(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepository.findByUsername(username);
        Movies movies = moviesRepository.findById(id).get();
        UserWatchList userWatchList = new UserWatchList();
        userWatchList.setUser(users);
        userWatchList.setMovie(movies);
        userWatchListRepository.save(userWatchList);
    }
}
