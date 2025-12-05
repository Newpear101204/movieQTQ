package com.movie.movie.model.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserDTO {
    String username;
    String password;
    String fullName;
    String phone;
    LocalDate dateOfBirth;
    String gender;
    String avatarUrl;
}
