package com.checkmate.users.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@Table(name = "user_settings")
public class UserSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_setting_id")
    private Long userSettingId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "setting_id")
    private Setting setting;

    @Column(name = "value")
    private String value;

    @CreationTimestamp
    @Column(name = "created_at")
    private String createdAt;
}
