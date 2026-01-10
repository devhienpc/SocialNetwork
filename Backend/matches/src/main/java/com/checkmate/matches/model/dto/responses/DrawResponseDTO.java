package com.checkmate.matches.model.dto.responses;

import lombok.Data;

@Data
public class DrawResponseDTO
{
    private long matchId;
    private boolean response; // true is accepted || false is denied
}
