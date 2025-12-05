package com.movie.movie.service.impl;

import com.movie.movie.entity.Genres;
import com.movie.movie.repository.GenresRepository;
import com.movie.movie.service.GenresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenresServiceImpl implements GenresService {

    @Autowired
    private GenresRepository genresRepository;

    @Override
    public List<Genres> getAllGenres() {
        return genresRepository.findAll();
    }
}
