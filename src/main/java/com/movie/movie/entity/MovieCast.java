package com.movie.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "movie_cast", indexes = {
        @Index(name = "idx_movie", columnList = "movie_id"),
        @Index(name = "idx_person", columnList = "person_id")
})
public class MovieCast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cast_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cast_movie"))
    Movies movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", referencedColumnName = "person_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cast_person"))
    Persons person;

    @Column(name = "character_name", length = 255)
    String characterName;

    @Column(name = "cast_order")
    Integer castOrder = 0;
}
