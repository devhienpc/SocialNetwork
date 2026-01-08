package com.checkmate.users.model.entity;

import jakarta.persistence.GeneratedValue;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "friendships")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId; // the user who sent the friend request

    @Column(name = "reciever_id", nullable = false)
    private Long receiverId; // the user who received the friend request


    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status = FriendshipStatus.PENDING;

    @Column(name = "accepted_at", nullable = true)
    private LocalDateTime acceptedAt;

    @Column(name = "unfriended_at", nullable = true)
    private LocalDateTime unfriendedAt;

    @Column(name = "blocked_at", nullable = true)
    private LocalDateTime blockedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum FriendshipStatus {
        PENDING,
        UNFRIENDED,
        BLOCKED,
        FRIENDS
    }
}
