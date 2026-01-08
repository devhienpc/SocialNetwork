package com.checkmate.users.model.dto.entity;

import lombok.Data;

@Data
public class FriendDTO {
    private Long userId;
    private String username;
    private String displayName;
    private Boolean currentlyActive = false;
}
