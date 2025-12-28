package com.movie.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "watch_history",
        indexes = {
                @Index(name = "idx_user", columnList = "user_id"),
                @Index(name = "idx_movie", columnList = "movie_id"),
                @Index(name = "idx_episode", columnList = "episode_id"),
                @Index(name = "idx_last_watched", columnList = "last_watched_at")
        })
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_history_user"))
    Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id",
            foreignKey = @ForeignKey(name = "fk_history_movie"))
    Movies movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", referencedColumnName = "episode_id",
            foreignKey = @ForeignKey(name = "fk_history_episode"))
    Episodes episode;

    @Column(name = "watch_duration")
    Integer watchDuration; // Thời gian đã xem (giây)

    @Column(name = "total_duration")
    Integer totalDuration; // Tổng thời lượng video (giây)

//    @Column(name = "progress_percentage", precision = 5, scale = 2)
//    BigDecimal progressPercentage; // Phần trăm hoàn thành

    @UpdateTimestamp
    @Column(name = "last_watched_at")
    LocalDateTime lastWatchedAt;

//    @Column(name = "completed")
//    Boolean completed = false;
}

