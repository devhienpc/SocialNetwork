package com.checkmate.users.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "preferences")
public class Preference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preference_id")
    private Long settingId;

    @Column(name = "name")
    private String name;
}
