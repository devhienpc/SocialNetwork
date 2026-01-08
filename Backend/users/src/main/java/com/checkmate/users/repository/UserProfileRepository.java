package com.checkmate.users.repository;

import com.checkmate.users.model.entity.User;
import com.checkmate.users.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

}
