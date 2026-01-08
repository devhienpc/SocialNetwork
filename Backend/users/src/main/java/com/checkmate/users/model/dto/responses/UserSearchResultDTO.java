package com.checkmate.users.model.dto.responses;

import lombok.Data;

@Data
public class UserSearchResultDTO {
    private long userId;
    private String displayName;
    private String username;

}
