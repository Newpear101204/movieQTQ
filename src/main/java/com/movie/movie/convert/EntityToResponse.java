package com.movie.movie.convert;

import com.movie.movie.entity.*;
import com.movie.movie.model.response.CastResponse;
import com.movie.movie.model.response.EpisodeResponse;
import com.movie.movie.model.response.MovieResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.util.Cast;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

@Component
public class EntityToResponse {

    @Autowired
    private ModelMapper modelMapper;

    public static MovieResponse convertFromMovie (Movies movie){
        MovieResponse movieResponse = new MovieResponse();
        movieResponse.setId(movie.getId());
        movieResponse.setTitle(movie.getTitle());
        movieResponse.setSubTitle(movie.getSubtTitle());
        movieResponse.setCountry(movie.getCountry().getCountryName());
        movieResponse.setImdb(movie.getImdbRating());
        movieResponse.setDuration(movie.getRuntime());
        movieResponse.setDescription(movie.getDescription());
        movieResponse.setRate(movie.getAgeRating());
        movieResponse.setSlug(movie.getSlug());
        movieResponse.setPoster(movie.getPosterUrl());
        movieResponse.setVideoUrl(movie.getVideoUrl());
        movieResponse.setYear(movie.getCreatedAt().getYear());
        movieResponse.setGenres(getGenres(movie.getMovieGenres()));
        if(movie.getVideoUrl() != null) {
            movieResponse.setType("phim-le");
        } else {
            movieResponse.setType("phim-bo");
            List<EpisodeResponse> episodeResponses = new ArrayList<>();
            List<Seasons> list = movie.getSeasons();
            for (Seasons season : list) {
                List<Episodes> episodes = season.getEpisodes();
                for (Episodes episode : episodes) {
                    EpisodeResponse episodeResponse = new EpisodeResponse();
                    episodeResponse.setId(episode.getId());
                    episodeResponse.setTitle(episode.getTitle());
                    episodeResponse.setVideoUrl(episode.getVideoUrl());
                    episodeResponses.add(episodeResponse);
                }
            }
            movieResponse.setEpisodes(episodeResponses);
        }
        List<MovieCast> movieCastsList = movie.getCasts();
        List<CastResponse> castResponseList = new ArrayList<>();
        for (MovieCast movieCast : movieCastsList) {
            Persons person = movieCast.getPerson();
            CastResponse castResponse = new CastResponse();
            castResponse.setId(person.getId());
            castResponse.setGender(person.getGender());
            castResponse.setImage(person.getAvatarUrl());
            castResponse.setSlug(person.getSlug());
            castResponse.setDob(person.getDateOfBirth());
            castResponse.setName(castResponse.getName());
            castResponseList.add(castResponse);
        }
        movieResponse.setCast(castResponseList);
        return movieResponse;
    }

    public static List<String> getGenres (List<MovieGenres> list) {
        List<String> genres = new ArrayList<>();
        for (MovieGenres movieGenres : list) {
            Genres genre = movieGenres.getGenre();
            genres.add(genre.getGenreName());
        }
        return genres;
    }


}
