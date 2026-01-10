package com.checkmate.matches.repository;

import com.checkmate.matches.model.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findMatchByMatchId(Long matchId);
    Optional<Match> findMatchByBlackUserId(Long blackUserId);
    Optional<Match> findMatchByWhiteUserId(Long whiteUserId);

    @Query(value = "SELECT * FROM matches WHERE (white_user_id = :userId OR black_user_id = :userId) AND (match_status = 'PENDING' OR match_status = 'PROGRESS')",
            nativeQuery = true)
    Optional<Match> findActiveMatchByUserId(@Param("userId") Long userId);


    @Query(value = "SELECT * FROM matches WHERE (white_user_id = :userId OR black_user_id = :userId)",
            nativeQuery = true)
    Optional<Match> findMatchesByUserId(@Param("userId") Long userId);


}
