package com.alphasolutions.eventapi.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role")
@Data
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Long idRole;

    @Column(name = "role", nullable = false, length = 15)
    private String role;

    public Role(long l, String participante) {
        this.idRole = l;
        this.role = participante;
    }
}