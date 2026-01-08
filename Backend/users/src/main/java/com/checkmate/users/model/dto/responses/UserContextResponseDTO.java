package com.checkmate.users.model.dto.responses;

import lombok.Data;

@Data
public class UserContextResponseDTO {

    private long userId;
    private String username;
    private String displayName;
    private Double coins;

}
