package com.movie.movie.entity;


import jakarta.persistence.*;
        import lombok.*;
        import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_watchlist",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_watchlist", columnNames = {"user_id", "movie_id"})
        },
        indexes = {
                @Index(name = "idx_user", columnList = "user_id"),
                @Index(name = "idx_movie", columnList = "movie_id")
        })
public class UserWatchList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "watchlist_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_watchlist_user"))
    Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_watchlist_movie"))
    Movies movie;

    @CreationTimestamp
    @Column(name = "added_at", updatable = false)
    LocalDateTime addedAt;
}
