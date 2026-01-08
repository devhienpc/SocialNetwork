package com.checkmate.users.repository;

import com.checkmate.users.model.entity.Friendship;
import com.checkmate.users.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    // Existing methods
    List<Friendship> findFriendshipsByRequesterId(long requesterId);
    List<Friendship> findFriendshipsByReceiverId(long receiverId);


    // Get if a friendship exists between two users
    Friendship findByRequesterIdAndReceiverId(Long requesterId, Long receiverId);

    // Get all sent friend requests for a user
    List<Friendship> findByRequesterIdAndStatus(Long userId, Friendship.FriendshipStatus status);

    // Get all pending friend requests for a user
    List<Friendship> findByReceiverIdAndStatus(Long userId, Friendship.FriendshipStatus status);

    // Get all friends related to a user
    List<Friendship> findByRequesterIdOrReceiverIdAndStatus(Long userId, Long userId2, Friendship.FriendshipStatus status);

    @Query("SELECT COUNT(f) FROM Friendship f WHERE (f.requesterId = :userId OR f.receiverId = :userId) AND f.status = :status")
    long countAllRelationshipsByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Friendship.FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.requesterId = :userId OR f.receiverId = :userId) AND f.status = :status")
    List<Friendship> getFriendsByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Friendship.FriendshipStatus status);

}
