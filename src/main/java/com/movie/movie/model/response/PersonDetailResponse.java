package com.movie.movie.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonDetailResponse {
    private Long id;
    private String name;
    private String slug;
    private String image;
    private String bio;
    private String gender;
    private LocalDate dob;

    private boolean isActorLove; // Trạng thái thích diễn viên

    // Danh sách phim của diễn viên này
    private List<MovieResponse> participatedMovies;
}
