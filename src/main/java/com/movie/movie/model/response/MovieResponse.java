package com.movie.movie.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponse {
    Long id;
    String title;
    String subTitle;
    String poster; // posterUrl
    BigDecimal imdb; // imdbRating
    String rate ; // tmdbRating
    Integer year ;
    Integer duration;
    String type;
    List<String> genres;
    String country;
    String slug;
    String videoUrl;
    String description;
    List<CastResponse> cast;
    List<EpisodeResponse> episodes;
    boolean isMovieLove;
}
