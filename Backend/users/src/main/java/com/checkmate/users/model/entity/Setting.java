package com.checkmate.users.model.entity;



import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "settings")
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long settingId;

    @Column(name = "name")
    private String name;
}
