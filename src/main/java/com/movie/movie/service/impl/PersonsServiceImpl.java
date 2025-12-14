package com.movie.movie.service.impl;

import com.movie.movie.convert.EntityToResponse;
import com.movie.movie.entity.MovieCast;
import com.movie.movie.entity.MovieCrew;
import com.movie.movie.entity.Movies;
import com.movie.movie.entity.Persons;
import com.movie.movie.model.response.CastResponse;
import com.movie.movie.model.response.MovieResponse;
import com.movie.movie.repository.PersonsRepository;
import com.movie.movie.service.PersonsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonsServiceImpl implements PersonsService {

    @Autowired
    private PersonsRepository personsRepository;

    @Override
    public List<CastResponse> getCast(String text) {
        List<Persons> persons = personsRepository.findByFullNameContaining(text);
        List<CastResponse> castResponses = new ArrayList<>();
        for (Persons person : persons) {
            CastResponse castResponse = new CastResponse();
            castResponse.setId(person.getId());
            castResponse.setDob(person.getDateOfBirth());
            castResponse.setSlug(person.getSlug());
            castResponse.setName(person.getFullName());
            castResponse.setImage(person.getAvatarUrl());
            castResponse.setGender(person.getGender());
            castResponses.add(castResponse);
        }
        return castResponses;
    }

    @Override
    public void deleteCast(Long id) {
        personsRepository.deleteById(id);
    }

    @Override
    public List<MovieResponse> getMoviesOfCast(Long id) {
        Persons persons = personsRepository.findById(id).get();
        List<MovieCast> movieCasts = persons.getMovieCasts();
        List<MovieResponse> movieResponses = new ArrayList<>();
        for (MovieCast movieCast : movieCasts) {
            Movies movies = movieCast.getMovie();
            MovieResponse movieResponse = EntityToResponse.convertFromMovie(movies);
            movieResponses.add(movieResponse);
        }
        return movieResponses;
    }
}
