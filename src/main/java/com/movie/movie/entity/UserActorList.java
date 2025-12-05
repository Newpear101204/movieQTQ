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
@Table(name = "user_actorlist",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_actorlist", columnNames = {"user_id", "person_id"})
        },
        indexes = {
                @Index(name = "idx_user", columnList = "user_id"),
                @Index(name = "idx_person", columnList = "person_id")
        })
public class UserActorList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actorlist_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_actorlist_user"))
    Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", referencedColumnName = "person_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_actorlist_person"))
    Persons person;

    @CreationTimestamp
    @Column(name = "added_at", updatable = false)
    LocalDateTime addedAt;
}