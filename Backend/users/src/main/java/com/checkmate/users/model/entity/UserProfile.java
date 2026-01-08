package com.checkmate.users.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_profile_id")
    private Long userProfileId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "biography")
    private String userBiography;
}
