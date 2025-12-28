package com.movie.movie.repository;

import com.movie.movie.entity.Movies;
import com.movie.movie.entity.Persons;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MoviesRepository extends JpaRepository <Movies, Long> {
    List<Movies> findByTitleContaining(String keyword);
    Movies findBySlug(String slug);

    @Modifying
    @Transactional
    @Query("UPDATE Movies m SET m.viewCount = COALESCE(m.viewCount, 0) + 1 WHERE m.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // 👇 Query tìm phim theo (Genre HOẶC Actor) VÀ (Chưa xem)
    @Query("SELECT DISTINCT m FROM Movies m " +
            "LEFT JOIN m.movieGenres mg " +
            "LEFT JOIN m.casts c " +
            "WHERE " +
            "   (mg.genre.id IN :genreIds OR c.person.id IN :actorIds) " + // Logic OR quan trọng
            "   AND m.id NOT IN :watchedIds " +
            "ORDER BY m.viewCount DESC")
    List<Movies> findMoviesByGenresOrActorsAndNotWatched(
            @Param("genreIds") List<Long> genreIds,
            @Param("actorIds") List<Long> actorIds,
            @Param("watchedIds") List<Long> watchedIds,
            Pageable pageable
    );

    List<Movies> findTop10ByOrderByViewCountDesc();

    boolean existsBySlug(String slug);
}
