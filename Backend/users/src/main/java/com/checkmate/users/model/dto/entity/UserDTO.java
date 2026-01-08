package com.checkmate.users.model.dto.entity;

import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String username;
    private String displayName;
}
