package com.movie.movie.service;

import com.movie.movie.model.response.CastResponse;

import java.util.List;

public interface PersonsService {
    List<CastResponse> getCast(String text);
    void deleteCast(Long id);
}
