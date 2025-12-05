package com.movie.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "persons", indexes = {
        @Index(name = "idx_slug", columnList = "slug"),
        @Index(name = "idx_name", columnList = "full_name")
})
public class Persons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    Long id;

    @Column(name = "full_name", nullable = false, length = 255)
    String fullName;

    @Column(name = "slug", nullable = false, unique = true, length = 255)
    String slug;

    @Column(name = "avatar_url", length = 500)
    String avatarUrl;

    @Column(name = "gender", length = 10)
    String gender;

    @Column(name = "biography", columnDefinition = "TEXT")
    String biography;

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", referencedColumnName = "country_id", foreignKey = @ForeignKey(name = "fk_person_country"))
    Countries country;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MovieCast> movieCasts;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MovieCrew> movieCrews;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserActorList> userActorLists;


}
