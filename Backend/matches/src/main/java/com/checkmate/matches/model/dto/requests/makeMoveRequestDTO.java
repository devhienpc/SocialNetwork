package com.checkmate.matches.model.dto.requests;

import lombok.Data;

@Data
public class makeMoveRequestDTO {
    private String piecePosition;
    private String toPosition;
}
