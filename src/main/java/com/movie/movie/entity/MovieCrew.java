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
@Table(name = "movie_crew", indexes = {
        @Index(name = "idx_movie", columnList = "movie_id"),
        @Index(name = "idx_person", columnList = "person_id"),
        @Index(name = "idx_role", columnList = "role")
})
public class MovieCrew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_crew_movie"))
    Movies movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", referencedColumnName = "person_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_crew_person"))
    Persons person;

    @Column(name = "role", nullable = false, length = 50)
    String role; // director, writer, producer, cinematographer, composer, editor, other

    @Column(name = "department", length = 100)
    String department;
}

