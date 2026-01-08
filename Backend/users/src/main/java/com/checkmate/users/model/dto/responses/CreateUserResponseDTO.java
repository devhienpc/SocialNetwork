package com.checkmate.users.model.dto.responses;

import lombok.Data;

@Data
public class CreateUserResponseDTO {
    private boolean failure;
    private String userId;
}
