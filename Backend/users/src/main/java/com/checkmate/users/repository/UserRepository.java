package com.checkmate.users.repository;

import com.checkmate.users.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCredentialId(Long credentialId);

    List<User> findUserByUsernameContainingIgnoreCase(String serachQuery);
}
