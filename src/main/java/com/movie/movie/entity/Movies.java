package com.movie.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "movies", indexes = {
        @Index(name = "idx_slug", columnList = "slug"),
        @Index(name = "idx_title", columnList = "title"),
        @Index(name = "idx_release_date", columnList = "release_date"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_type", columnList = "type"),
        @Index(name = "idx_featured", columnList = "is_featured"),
        @Index(name = "idx_trending", columnList = "is_trending")
})
public class Movies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    Long id;

    @Column(name = "title", nullable = false, length = 500)
    String title;

    @Column(name = "sub_title", nullable = false, length = 500)
    String subtTitle;

//    @Column(name = "original_title", length = 500)
//    String originalTitle;

    @Column(name = "slug", nullable = false, unique = true, length = 500)
    String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "poster_url", length = 500)
    String posterUrl;

//    @Column(name = "backdrop_url", length = 500)
//    String backdropUrl;

    @Column(name = "trailer_url", length = 500)
    String trailerUrl;

    @Column(name = "release_date")
    LocalDate releaseDate;

    @Column(name = "runtime")
    Integer runtime; // Thời lượng tính bằng phút

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", referencedColumnName = "country_id", foreignKey = @ForeignKey(name = "fk_movie_country"))
    Countries country;

//    @Column(name = "language", length = 50)
//    String language;

    @Column(name = "status", length = 50)
    String status ; // coming_soon, now_showing, completed

//    @Column(name = "type", length = 50)
//    String type ; // movie, series, tv_show

//    @Column(name = "quality", length = 20)
//    String quality ; // HD, Full HD, 4K, CAM, SD

    @Column(name = "imdb_rating", precision = 3, scale = 1)
    BigDecimal imdbRating;

//    @Column(name = "tmdb_rating", precision = 3, scale = 1)
//    BigDecimal tmdbRating;

    @Column(name = "view_count")
    Long viewCount = 0L;

//    @Column(name = "is_featured")
//    Boolean isFeatured = false;

    @Column(name = "is_trending")
    Boolean isTrending = false;

    @Column(name = "video_url", length = 500)
    String videoUrl;

    @Column(name = "age_rating", length = 10)
    String ageRating; // PG, PG-13, R, etc.

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    List<WatchHistory> watchHistories;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserWatchList> watchLists;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Seasons> seasons;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MovieCast> casts;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MovieCrew> crews;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MovieGenres> movieGenres;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comments> comments;

}
