package com.checkmate.users.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@Table(name = "user_preferences")
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_preference_id")
    private Long userSettingId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "preference_id")
    private Preference preference;

    @Column(name = "value")
    private String value;

    @CreationTimestamp
    @Column(name = "created_at")
    private String createdAt;
}
