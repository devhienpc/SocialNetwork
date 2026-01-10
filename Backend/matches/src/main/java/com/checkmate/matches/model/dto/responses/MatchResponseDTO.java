package com.checkmate.matches.model.dto.responses;

import lombok.Data;

@Data
public class MatchResponseDTO {

    private Long matchId;
    private Long whiteUserId;
    private Long blackUserId;
    private String matchStatus;
    private String matchType;
    private String matchMoves;

    private Boolean isFinished;
    private Boolean isAbandoned;
    private Boolean isForfeited;
    private Boolean isRated;
    private Boolean isDraw;


    private Long winnerUserId;
    private Boolean drawRequested;
    private Long drawRequesterId;


    private Boolean isWhiteTurn;
    private String currentBoard;
    private Long whiteTimeRemaining;
    private Long blackTimeRemaining;

    private Long lastWhitePing;
    private Long lastBlackPing;
    private Long lastMoveTime;
}
