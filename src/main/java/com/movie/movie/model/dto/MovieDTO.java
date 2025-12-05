package com.movie.movie.model.dto;


import com.movie.movie.entity.Countries;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieDTO {
    String title;
    String originalTitle;
    String slug;
    String description;
    String posterUrl;
    String backdropUrl;
    String trailerUrl;
    LocalDate releaseDate;
    Integer runtime;
    Integer countryId;
    String type ;
    String ageRating;
}
