package com.checkmate.users.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_credential_id")
    private Long credentialId;

    @Column(name = "username")
    private String username;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "Coins")
    private double coinTotal;


    @OneToOne
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserPreference> preferences = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserSetting> settings = new HashSet<>();
}
