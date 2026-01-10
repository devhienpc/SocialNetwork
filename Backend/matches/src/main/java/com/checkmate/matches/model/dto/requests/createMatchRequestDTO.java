package com.checkmate.matches.model.dto.requests;

import lombok.Data;

@Data
public class createMatchRequestDTO {
    private Long whiteUserId;
    private Long blackUserId;
}
