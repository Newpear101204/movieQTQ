package com.movie.movie.service;

import com.movie.movie.model.response.MovieResponse;

import java.util.List;

public interface RecommendService {
    List<MovieResponse> getRecommendedMovies();
}
