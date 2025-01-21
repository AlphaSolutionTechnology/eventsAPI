package com.alphasolutions.eventapi.model;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "\"user\"")
@Data
public class User {
    @Id
    @Column(name = "id_user",nullable = false)
    private String id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "id_role", referencedColumnName = "id_role", nullable = true)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "id_evento", referencedColumnName = "id_evento", nullable = true)
    private Evento evento;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "redesocial", length = 80)
    private String redeSocial;

    @Column(name = "unique_code",updatable = false,nullable = false)
    private Long uniqueCode;

    public User() {}
    public User(String id,String nome,String email, Role role, Evento evento, String redeSocial) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.evento = evento;
        this.redeSocial = redeSocial;
    }
}
