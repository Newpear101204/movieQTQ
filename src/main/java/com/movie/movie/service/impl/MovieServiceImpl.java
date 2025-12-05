package com.movie.movie.service.impl;

import com.movie.movie.convert.EntityToResponse;
import com.movie.movie.entity.*;
import com.movie.movie.model.response.CastResponse;
import com.movie.movie.model.response.DirectorResponse;
import com.movie.movie.model.response.MovieResponse;
import com.movie.movie.model.response.PersonResponse;
import com.movie.movie.repository.MoviesRepository;
import com.movie.movie.repository.UsersRepository;
import com.movie.movie.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MoviesRepository moviesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public List<MovieResponse> getMovie(String text) {
        List<Movies> moviesList = moviesRepository.findByTitleContaining(text);
        List<MovieResponse> movieResponseList = new ArrayList<>();
        for (Movies movie : moviesList) {
            MovieResponse movieResponse = EntityToResponse.convertFromMovie(movie);
            movieResponseList.add(movieResponse);
        }

        return movieResponseList;
    }

    @Override
    public List<MovieResponse> getHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepository.findByUsername(username);
        List<MovieResponse> movieResponseList = new ArrayList<>();
        List<WatchHistory> historyList = users.getWatchHistories();
        for (WatchHistory watchHistory : historyList) {
            Movies movies = watchHistory.getMovie();
            MovieResponse movieResponse = EntityToResponse.convertFromMovie(movies);
            movieResponseList.add(movieResponse);
        }
        return movieResponseList;
    }

    @Override
    public void deleteMovie(Long id) {
        moviesRepository.deleteById(id);
    }

    @Override
    public PersonResponse getPersons(Long id) {
        Movies movies = moviesRepository.findById(id).get();
        List<MovieCast> movieCasts = movies.getCasts();
        List<MovieCrew> movieCrews = movies.getCrews();
        List<CastResponse> castResponses = new ArrayList<>();
        List<DirectorResponse> directorResponses = new ArrayList<>();
        for (MovieCast movieCast : movieCasts) {
            Persons person = movieCast.getPerson();
            CastResponse castResponse = new CastResponse(person.getId(), person.getFullName(),
                    person.getSlug(), person.getAvatarUrl(), person.getGender(),person.getDateOfBirth());
            castResponses.add(castResponse);
        }

        for (MovieCrew movieCrew : movieCrews) {
            Persons person = movieCrew.getPerson();
            DirectorResponse directorResponse = new DirectorResponse(person.getId(), person.getFullName(),
                    person.getSlug(), person.getAvatarUrl(), person.getGender(),person.getDateOfBirth());
            directorResponses.add(directorResponse);
        }
        return new PersonResponse(castResponses, directorResponses);
    }
}
