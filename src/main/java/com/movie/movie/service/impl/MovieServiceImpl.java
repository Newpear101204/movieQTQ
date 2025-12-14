package com.movie.movie.service.impl;

import com.movie.movie.convert.EntityToResponse;
import com.movie.movie.entity.*;
import com.movie.movie.model.response.*;
import com.movie.movie.repository.*;
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

    @Autowired
    private CountriesRepository countriesRepository;

    @Autowired
    private PersonsRepository personsRepository;

    @Autowired
    private EpisodeRepsitory episodeRepository;

    @Autowired
    private GenresRepository genresRepository;




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

    @Override
    public void addMovie(MovieResponse movie) {
        Movies movies = new Movies();
        movies.setTitle(movie.getTitle());
        movies.setDescription(movie.getDescription());
        movies.setCountry(countriesRepository.findByCountryName(movie.getCountry()));
        movies.setPosterUrl(movie.getPoster());
        movies.setRuntime(movie.getDuration());
        movies.setSlug(movie.getSlug());
        movies.setSubtTitle(movie.getSubTitle());
        movies.setVideoUrl(movie.getVideoUrl());
        List<MovieCast> listMovieCast = new ArrayList<>();
        for(CastResponse cast : movie.getCast() ){
            Persons persons = personsRepository.findById(cast.getId()).get();
            MovieCast movieCast = new MovieCast();
            movieCast.setPerson(persons);
            movieCast.setMovie(movies);
        }
        movies.setCasts(listMovieCast);
        List<Episodes> episodesList = new ArrayList<>();
        for (EpisodeResponse episode : movie.getEpisodes()) {
            Episodes episodes = new Episodes();
            episodes.setTitle(episode.getTitle());
            episodes.setVideoUrl(episode.getVideoUrl());
            episodeRepository.save(episodes);
            episodesList.add(episodes);
        }
        Seasons seasons = new Seasons();
        seasons.setTitle(movie.getTitle());
        seasons.setMovie(movies);
        seasons.setEpisodes(episodesList);
        List<Seasons> seasonsList = new ArrayList<>();
        seasonsList.add(seasons);
        movies.setSeasons(seasonsList);

        List<MovieGenres> movieGenres = new ArrayList<>();
        for(String genre : movie.getGenres()){
            Genres genres = genresRepository.findByGenreName(genre);
            MovieGenres movieGenre = new MovieGenres();
            movieGenre.setGenre(genres);
            movieGenre.setMovie(movies);
            movieGenres.add(movieGenre);
        }
        movies.setMovieGenres(movieGenres);
        moviesRepository.save(movies);

    }
}
