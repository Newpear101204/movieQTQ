package com.movie.movie.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CastResponse {
    Long id;
    String name; // fullName
    String slug;
    String image;  // avatarUrl
    String gender;
    LocalDate dob ; // DateOfBirth
}
