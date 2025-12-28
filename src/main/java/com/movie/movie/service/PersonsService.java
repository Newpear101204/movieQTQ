package com.movie.movie.service;

import com.movie.movie.model.response.CastResponse;
import com.movie.movie.model.response.MovieResponse;
import com.movie.movie.model.response.PersonDetailResponse;

import java.util.List;

public interface PersonsService {
    List<CastResponse> getCast(String text);
    void deleteCast(Long id);
    List<MovieResponse> getMoviesOfCast(Long id);
    PersonDetailResponse getPersonDetail(String slug);
}
