package com.movie.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comments",
        indexes = {
                @Index(name = "idx_user", columnList = "user_id"),
                @Index(name = "idx_movie", columnList = "movie_id"),
                @Index(name = "idx_episode", columnList = "episode_id"),
                @Index(name = "idx_parent", columnList = "parent_comment_id"),
                @Index(name = "idx_created", columnList = "created_at")
        })
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_comment_user"))
    Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id",
            foreignKey = @ForeignKey(name = "fk_comment_movie"))
    Movies movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", referencedColumnName = "episode_id",
            foreignKey = @ForeignKey(name = "fk_comment_episode"))
    Episodes episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", referencedColumnName = "comment_id",
            foreignKey = @ForeignKey(name = "fk_comment_parent"))
    Comments parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comments> replies; // Danh sách phản hồi cho bình luận

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    String content;

    @Column(name = "likes_count")
    Integer likesCount = 0;

    @Column(name = "is_approved")
    Boolean isApproved = true;

    @Column(name = "is_spoiler")
    Boolean isSpoiler = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}

