package com.movie.movie.service;

import com.movie.movie.model.response.MovieResponse;
import com.movie.movie.model.response.PersonResponse;

import java.util.List;

public interface MovieService {
    List<MovieResponse> getMovie(String text);
    List<MovieResponse> getHistory();
    void deleteMovie(Long id);
    PersonResponse getPersons (Long id);
}
