package com.checkmate.matches.controller;

import com.checkmate.matches.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.checkmate.matches.model.dto.requests.createMatchRequestDTO;
import com.checkmate.matches.model.dto.responses.DrawResponseDTO;
import com.checkmate.matches.model.dto.responses.MatchResponseDTO;
import com.checkmate.matches.model.entity.Match;
import com.checkmate.matches.repository.MatchRepository;
import com.checkmate.matches.security.OptionalJWT;
import com.checkmate.matches.security.PermissionRequired;
import com.checkmate.matches.security.RequiresJWT;
import com.checkmate.matches.service.MatchService;
import com.netflix.discovery.converters.Auto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
public class MatchController {

    private static final Logger logger = LoggerFactory.getLogger(MatchController.class);

    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private MatchService matchService;

    @GetMapping("/getCurrentMatch")
    @RequiresJWT
    public ResponseEntity<?> getCurrentMatch(HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");

        if (decodedJWT == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing JWT token.");
        }

        Optional<Match> match = matchRepository.findActiveMatchByUserId(decodedJWT.getClaim("userId").asLong());

        if (match.isEmpty()) {
            matchService.debugUserMatches(decodedJWT.getClaim("userId").asLong());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No active match found.");
        }

        return ResponseEntity.ok(match.get().getMatchId());
    }

    @PostMapping("/pingMatch")
    @OptionalJWT
    @Transactional
    public ResponseEntity<?> pingMatch(@RequestParam("matchId") long matchId, HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");

        Match match;

        if (decodedJWT != null) {

            long userId = decodedJWT.getClaim("userId").asLong();

            match = matchService.pingMatch(matchId, userId);


        } else {
            match = matchService.pingMatch(matchId, -1);
        }

        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Match not found.");
        }

        return ResponseEntity.ok(formatMatchResponseDTO(match));
    }

    private MatchResponseDTO formatMatchResponseDTO(Match match) {
        MatchResponseDTO matchResponseDTO = new MatchResponseDTO();

        matchResponseDTO.setMatchId(match.getMatchId());
        matchResponseDTO.setMatchStatus(String.valueOf(match.getMatchStatus()));
        matchResponseDTO.setMatchType(String.valueOf(match.getMatchType()));
        matchResponseDTO.setMatchMoves(match.getMatchMoves());
        matchResponseDTO.setIsFinished(match.isFinished());
        matchResponseDTO.setIsAbandoned(match.isAbandoned());
        matchResponseDTO.setIsForfeited(match.isForfeited());
        matchResponseDTO.setIsRated(match.isRated());
        matchResponseDTO.setIsDraw(match.isDraw());
        matchResponseDTO.setDrawRequested(match.isDrawRequested());
        matchResponseDTO.setDrawRequesterId(match.getDrawRequesterId());
        matchResponseDTO.setIsWhiteTurn(match.getIsWhiteTurn());
        matchResponseDTO.setCurrentBoard(match.getCurrentBoard());
        matchResponseDTO.setLastWhitePing(match.getLastWhitePing());
        matchResponseDTO.setLastBlackPing(match.getLastBlackPing());
        matchResponseDTO.setWhiteUserId(match.getWhiteUserId());
        matchResponseDTO.setBlackUserId(match.getBlackUserId());
        matchResponseDTO.setWhiteTimeRemaining(match.getWhiteTimeLeft());
        matchResponseDTO.setBlackTimeRemaining(match.getBlackTimeLeft());
        matchResponseDTO.setIsWhiteTurn(match.getIsWhiteTurn());

        matchResponseDTO.setWinnerUserId(match.getWinnerUserId());
        return matchResponseDTO;
    }

    @PostMapping("/makeMove")
    @RequiresJWT
    public ResponseEntity<?> makeMove(@RequestParam("matchId") String matchId, @RequestParam("move") String move, HttpServletRequest request)
    {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");

        if (decodedJWT == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing JWT token.");
        }

        long userId = decodedJWT.getClaim("userId").asLong();


        boolean moveMade = matchService.makeMove(matchId, userId, move);

        if (!moveMade) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid move.");
        }

        return ResponseEntity.ok("Move made.");

    }

    @PostMapping("/resignMatch")
    @RequiresJWT
    public ResponseEntity<?> forfeitMatch(@RequestParam("matchId") String matchId, HttpServletRequest request)
    {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");


        boolean attempt = matchService.resignRequest(Long.parseLong(matchId), decodedJWT.getClaim("userId").asLong());

        if (!attempt) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request.");
        }

        return ResponseEntity.ok("Match forfeited.");

    }

    @PostMapping("/requestDraw")
    @RequiresJWT
    public ResponseEntity<?> requestDraw(@RequestParam("matchId") String matchId, HttpServletRequest request)
    {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");

        boolean attempt = matchService.drawRequest(Long.parseLong(matchId), decodedJWT.getClaim("userId").asLong());

        if (!attempt) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request.");
        }

        return ResponseEntity.ok("Draw request response received.");

    }

    // accept draw
    @PostMapping("/acceptDraw")
    @RequiresJWT
    public ResponseEntity<?> acceptDraw(@RequestParam("matchId") String matchId, HttpServletRequest request)
    {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");

        boolean attempt = matchService.acceptDraw(Long.parseLong(matchId), decodedJWT.getClaim("userId").asLong());

        if (!attempt) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request.");
        }

        return ResponseEntity.ok("Draw request response received.");

    }

    // decline draw
    @PostMapping("/declineDraw")
    @RequiresJWT
    public ResponseEntity<?> declineDraw(@RequestParam("matchId") String matchId, HttpServletRequest request)
    {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");

        boolean attempt = matchService.declineDraw(Long.parseLong(matchId), decodedJWT.getClaim("userId").asLong());

        if (!attempt) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request.");
        }

        return ResponseEntity.ok("Draw request response received.");

    }


}
