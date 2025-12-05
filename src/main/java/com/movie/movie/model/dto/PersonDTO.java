package com.movie.movie.model.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonDTO {
    String fullName;
    String avatarUrl;
    String biography;
    LocalDate dateOfBirth;
    Integer countryId;
}
