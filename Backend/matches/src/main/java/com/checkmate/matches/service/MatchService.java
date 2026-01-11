package com.checkmate.matches.service;


import com.checkmate.matches.config.RabbitMqConfig;
import com.checkmate.matches.controller.MatchController;
import com.checkmate.matches.model.entity.Match;
import com.checkmate.matches.model.rabbitmq.MatchEventDTO;
import com.checkmate.matches.model.rabbitmq.MatchEventType;
import com.checkmate.matches.model.rabbitmq.UserEventDTO;
import com.checkmate.matches.model.rabbitmq.UserEventType;
import com.checkmate.matches.model.util.Game.Game;
import com.checkmate.matches.model.util.Game.Move;
import com.checkmate.matches.repository.MatchRepository;
import com.checkmate.matches.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.checkmate.matches.*;

import java.time.Instant;
import java.util.Optional;

@Service
public class MatchService {

    // Logger

    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    @Autowired
    private MatchRepository matchRepository;

    //@Autowired
    //private UserRepository userRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }

    /***
     * Creates a match between two users and currently assumes the match type
     * @param User1Id
     * @param User2Id
     */
    public void createMatch(Long User1Id, Long User2Id) {
        logger.info("Creating match between " + User1Id + " and " + User2Id);

        // randomly pick if user1 or user2 is white
        boolean user1IsWhite = Math.random() < 0.5;

        // create match object
        Match newMatch = new Match();
        newMatch.setWhiteUserId(user1IsWhite ? User1Id : User2Id);
        newMatch.setBlackUserId(user1IsWhite ? User2Id : User1Id);

        newMatch.setMatchType(Match.MatchType.BLITZ);

        newMatch.setWhiteTimeLeft(5 * 60L);
        newMatch.setBlackTimeLeft(5 * 60L);


        matchRepository.save(newMatch);
    }

    public void createMatch(Long User1Id, Long User2Id, double ante) {
        logger.info("Creating match between " + User1Id + " and " + User2Id + "for " + ante);

        // randomly pick if user1 or user2 is white
        boolean user1IsWhite = Math.random() < 0.5;

        // create match object
        Match newMatch = new Match();
        newMatch.setWhiteUserId(user1IsWhite ? User1Id : User2Id);
        newMatch.setBlackUserId(user1IsWhite ? User2Id : User1Id);

        newMatch.setMatchType(Match.MatchType.BLITZ);

        newMatch.setWhiteTimeLeft(5 * 60L);
        newMatch.setBlackTimeLeft(5 * 60L);
        newMatch.setAnte(ante);


        matchRepository.save(newMatch);
    }



    public Match pingMatch(long matchId, long userId) {
        Optional<Match> optionalMatch = matchRepository.findById(matchId);
        // match doesnt exist
        if (optionalMatch.isEmpty()) {
            return null;
        }

        Match thisMatch = optionalMatch.get();

        // match is finished
        if (thisMatch.getMatchStatus() == Match.MatchStatus.FINISHED) {
            return thisMatch;
        }

        // match is abandoned
        if (thisMatch.isAbandoned()) {
            return thisMatch;
        }

        // match is forfeited
        if (thisMatch.isForfeited()) {
            return thisMatch;
        }

        // match is draw
        if (thisMatch.isDraw()) {
            return thisMatch;
        }

        if (thisMatch.getWhiteUserId() == userId) {
            thisMatch.setLastWhitePing(Instant.now().getEpochSecond());
            thisMatch = matchRepository.saveAndFlush(thisMatch);
        } else if (thisMatch.getBlackUserId() == userId) {
            thisMatch.setLastBlackPing(Instant.now().getEpochSecond());
            thisMatch = matchRepository.saveAndFlush(thisMatch);
        }

        // start the match if both users have pinged and match is pending
        if (thisMatch.getLastWhitePing() != null && thisMatch.getLastBlackPing() != null && thisMatch.getMatchStatus() == Match.MatchStatus.PENDING) {
            thisMatch.setMatchStatus(Match.MatchStatus.PROGRESS);
            thisMatch.setMatchStartTime(Instant.now().getEpochSecond());
            thisMatch = matchRepository.saveAndFlush(thisMatch);
        }

        return thisMatch;
    }

    public void finishMatch(Match match) {
        // post to rabbitmq
        //before finishing the match transfer coins

       if(match.getAnte() > 0 && !match.isDraw())
       {
           double ante = match.getAnte();
           long winnerID = match.getWinnerUserId();
           long loserID;
           if(winnerID == match.getWhiteUserId())
           {
               loserID = match.getBlackUserId();
           }
           else
           {
               loserID = match.getWhiteUserId();
           }
           UserEventDTO winnerUpdate = new UserEventDTO();
           winnerUpdate.setEventType(UserEventType.ADD_USER_COINS);
           winnerUpdate.setUserId(winnerID);
           winnerUpdate.addAdditionalDetails("amount", Double.toString(ante));

           rabbitTemplate.convertAndSend(RabbitMqConfig.USER_EVENTS_EXCHANGE, "", winnerUpdate);

           UserEventDTO loserUpdate = new UserEventDTO();
           loserUpdate.setEventType(UserEventType.REMOVE_USER_COINS);
           loserUpdate.setUserId(loserID);
           loserUpdate.addAdditionalDetails("amount", Double.toString(ante));

           rabbitTemplate.convertAndSend(loserUpdate);

       }



        MatchEventDTO matchEventDTO = new MatchEventDTO();
        matchEventDTO.setEventType(MatchEventType.MATCH_FINISHED);
        matchEventDTO.addAdditionalDetails("matchId", Long.toString(match.getMatchId()));
        matchEventDTO.addAdditionalDetails("whiteUserId", String.valueOf(match.getWhiteUserId()));
        matchEventDTO.addAdditionalDetails("blackUserId", String.valueOf(match.getBlackUserId()));

        rabbitTemplate.convertAndSend(RabbitMqConfig.MATCHMAKING_MICROSERVICE_DIRECT_EXCHANGE, "matchmaking_microservice_direct_queue", matchEventDTO);
    }

    public void debugUserMatches(long userId) {
        // post to rabbitmq

        MatchEventDTO matchEventDTO = new MatchEventDTO();
        matchEventDTO.setEventType(MatchEventType.DEBUG_USER_MATCHES);
        matchEventDTO.addAdditionalDetails("userId", String.valueOf(userId));
        rabbitTemplate.convertAndSend(RabbitMqConfig.MATCHMAKING_MICROSERVICE_DIRECT_EXCHANGE, "matchmaking_microservice_direct_queue", matchEventDTO);

    }


    public boolean resignRequest(long matchId, long userId) {
        Optional<Match> optionalMatch = matchRepository.findMatchByMatchId(matchId);

        if (optionalMatch.isEmpty()) {
            return false;
        }

        Match thisMatch = optionalMatch.get();

        // is user in the match

        if (thisMatch.getWhiteUserId() != userId && thisMatch.getBlackUserId() != userId) {
            return false;
        }

        boolean isWhite;
        long otherUser;
        if (thisMatch.getBlackUserId() == userId) {
            isWhite = false;
            otherUser = thisMatch.getWhiteUserId();
        } else {
            isWhite = true;
            otherUser = thisMatch.getBlackUserId();
        }

        thisMatch.setForfeited(true);
        thisMatch.setWinnerUserId(otherUser);
        thisMatch.setFinished(true);
        thisMatch.setMatchStatus(Match.MatchStatus.FINISHED);
        matchRepository.saveAndFlush(thisMatch);
        finishMatch(thisMatch);
        return true;
    }

    public boolean acceptDraw(long matchId, long userId) {
        Optional<Match> optionalMatch = matchRepository.findMatchByMatchId(matchId);
        if (optionalMatch.isEmpty()) {
            return false;
        }

        Match thisMatch = optionalMatch.get();

        // is user in match
        if (thisMatch.getWhiteUserId() != userId || thisMatch.getBlackUserId() != userId) {
            return false;
        }

        if (!thisMatch.isDrawRequested()) {
            return false;
        }

        if (thisMatch.getDrawRequesterId() == userId) {
            return false;
        }

        thisMatch.setDraw(true);
        thisMatch.setFinished(true);
        thisMatch.setMatchStatus(Match.MatchStatus.FINISHED);
        matchRepository.saveAndFlush(thisMatch);
        finishMatch(thisMatch);

        return true;
    }

    public boolean declineDraw(long matchId, long userId) {
        Optional<Match> optionalMatch = matchRepository.findMatchByMatchId(matchId);
        if (optionalMatch.isEmpty()) {
            return false;
        }

        Match thisMatch = optionalMatch.get();

        // is user in match
        if (thisMatch.getWhiteUserId() != userId || thisMatch.getBlackUserId() != userId) {
            return false;
        }

        if (!thisMatch.isDrawRequested()) {
            return false;
        }

        if (thisMatch.getDrawRequesterId() == userId) {
            return false;
        }

        thisMatch.setDrawRequested(false);
        thisMatch.setDrawRequesterId(null);
        matchRepository.saveAndFlush(thisMatch);
        return true;
    }

    public boolean drawResponse(long matchId, long userId, boolean response) {
        Optional<Match> optionalMatch = matchRepository.findActiveMatchByUserId(matchId);
        if (optionalMatch.isEmpty()) {
            return false;
        }

        Match thisMatch = optionalMatch.get();

        // is user in match
        if (thisMatch.getWhiteUserId() != userId || thisMatch.getBlackUserId() != userId) {
            return false;
        }

        if (!thisMatch.isDrawRequested()) {
            return false;
        }

        if (thisMatch.getDrawRequesterId() == userId) {
            return false;
        }

        if (!response) {
            thisMatch.setDrawRequested(false);
            thisMatch.setDrawRequesterId(null);
            matchRepository.saveAndFlush(thisMatch);
            return true;
        }

        thisMatch.setDraw(true);
        thisMatch.setFinished(true);
        thisMatch.setMatchStatus(Match.MatchStatus.FINISHED);
        matchRepository.saveAndFlush(thisMatch);
        finishMatch(thisMatch);

        return true;

    }

    public boolean drawRequest(long matchId, long userId) {
        Optional<Match> optionalMatch = matchRepository.findMatchByMatchId(matchId);
        if (optionalMatch.isEmpty()) {
            return false;
        }

        Match myMatch = optionalMatch.get();

        // is user in match

        if (myMatch.getWhiteUserId() != userId && myMatch.getBlackUserId() != userId) {
            return false;
        }

        if (myMatch.isDrawRequested() && myMatch.getDrawRequesterId() != userId) {
            // draw is requested so accept it since mutual.
            myMatch.setDraw(true);
            myMatch.setFinished(true);
            myMatch.setMatchStatus(Match.MatchStatus.FINISHED);
            matchRepository.saveAndFlush(myMatch);
            finishMatch(myMatch);
            return true;
        }

        myMatch.setDrawRequested(true);
        myMatch.setDrawRequesterId(userId);
        matchRepository.saveAndFlush(myMatch);
        return true;

    }


    public boolean makeMove(String matchId, long userIdS, String move) {
        System.out.println("makeMove on match " + matchId + " called by " + userIdS + " with move " + move);

        // If the game doesn't exist, we can't do anything
        Optional<Match> optionalMatch = matchRepository.findMatchByMatchId(Long.valueOf(matchId));

        if (optionalMatch.isEmpty()) {
            logger.info("Match " + matchId + " doesn't exist");
            return false;
        }

        Match match = optionalMatch.get();

        // If the game is finished, we can't do anything
        if (match.isFinished()) {
            logger.info("Match " + matchId + " is finished");
            return false;
        }

        if (match.getMatchStatus() != Match.MatchStatus.PROGRESS) {
            logger.info("Match " + matchId + " is not in progress");
            return false;
        }

        // If the user isn't in the game, we can't do anything

        if (match.getWhiteUserId() != Long.valueOf(userIdS) && match.getBlackUserId() != Long.valueOf(userIdS)) {
            logger.info("User " + userIdS + " is not in match " + matchId);
            return false;
        }

        // if if its not the user's turn, we can't do anything

        if (match.getIsWhiteTurn() && match.getWhiteUserId() != userIdS) {
            logger.info("User " + userIdS + " is not white in match " + matchId);
            return false;
        }

        if (!match.getIsWhiteTurn() && match.getBlackUserId() != userIdS) {
            logger.info("User " + userIdS + " is not black in match " + matchId);
            return false;
        }

        // If the move is invalid, we can't do anything

        logger.info("Making move " + move);

        Game myBoard = new Game(match.getCurrentBoard(), match.getIsWhiteTurn());

        myBoard.setWhiteTurn(match.getIsWhiteTurn());

        myBoard.printStatus();

        Move myMove = new Move(move);
        boolean moveWorked = myBoard.move(myMove);
        if (!moveWorked) {
            logger.info("Move " + move + " failed");
            return false;
        }

        logger.info("Move " + move + " succeeded");

        String endState = myBoard.getBoardNotation();

        logger.info("End state: " + endState);

        match.setCurrentBoard(endState);
        match.setMatchMoves(match.getMatchMoves() + move + ",");
        long now = Instant.now().getEpochSecond();
        long timeTaken = now - match.getLastMoveTime();
        if (userIdS == match.getWhiteUserId()) {
            long newTime = match.getWhiteTimeLeft() - timeTaken;
            match.setWhiteTimeLeft(newTime);
        } else {
            long newTime = match.getBlackTimeLeft() - timeTaken;
            match.setWhiteTimeLeft(newTime);
        }

        match.setIsWhiteTurn(!match.getIsWhiteTurn());
        match.setLastMoveTime(now);

        if (!endState.contains("k")) {
            match.setMatchStatus(Match.MatchStatus.FINISHED);
            match.setWinnerUserId(match.getWhiteUserId());
            match.setFinished(true);
            match.setMatchEndTime(Instant.now().getEpochSecond());
        }

        if (!endState.contains("K")) {
            match.setMatchStatus(Match.MatchStatus.FINISHED);
            match.setWinnerUserId(match.getBlackUserId());
            match.setFinished(true);
            match.setMatchEndTime(Instant.now().getEpochSecond());
        }

        matchRepository.saveAndFlush(match);
        return true;

        //get current match
        // check turn return null
        // all validation
        // create board
        //make move

    }


}
