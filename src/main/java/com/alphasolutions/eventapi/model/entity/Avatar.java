package com.alphasolutions.eventapi.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "avatar")
@Data
public class Avatar {

    @Id
    @Column(name = "avatar_seed")
    private String avatarSeed;

    @Column(name = "avatar_style")
    private String avatarStyle;
}