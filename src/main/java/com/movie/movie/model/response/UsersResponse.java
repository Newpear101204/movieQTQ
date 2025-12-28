package com.movie.movie.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsersResponse {
    Long id;
    String email;
    String username;
    String fullName;
    String avatarUrl;
    String phone;
    LocalDate dateOfBirth;
    String role ;
    Boolean isActive ;
    LocalDateTime createdAt;
    String gender;
}
