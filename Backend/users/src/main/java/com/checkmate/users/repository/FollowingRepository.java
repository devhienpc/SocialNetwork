package com.checkmate.users.repository;

import com.checkmate.users.model.entity.Following;
import com.checkmate.users.model.entity.Friendship;
import com.checkmate.users.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowingRepository extends JpaRepository<Following, Long> {

    List<Following> findByFollowerId(Long followerId);
    List<Following> findByFollowingId(Long followingId);
    Following findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // Get number of users a user is following
    long countByFollowerId(Long followerId);

    // Get number of followers for a user
    long countByFollowingId(Long followingId);
}
