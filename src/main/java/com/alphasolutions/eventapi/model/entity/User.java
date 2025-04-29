package com.alphasolutions.eventapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "id_user")
    private String idUser;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "id_role")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "id_evento")
    private Evento evento;

    @Column(name = "redesocial", length = 80)
    private String redesocial;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "unique_code", nullable = false)
    private String uniqueCode = "";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_email_verified")
    private Boolean isEmailVerified = true;

    @ManyToOne
    @JoinColumn(name = "palestra_atual")
    private Palestra palestraAtual;

    @OneToOne
    @JoinColumn(name = "avatar_seed")
    private Avatar avatar;

    public User(String id, String nome, Role role, Evento evento, String email, String redeSocial, String uniqueCode) {
        this.idUser = id;
        this.nome = nome;
        this.role = role;
        this.evento = evento;
        this.email = email;
        this.redesocial = redeSocial;
        this.uniqueCode = uniqueCode;
    }

    public User(String id, String username, Role role, Evento evento, String email, String redesocial, String password, String uniqueCode) {
        this.idUser = id;
        this.nome = username;
        this.role = role;
        this.evento = evento;
        this.email = email;
        this.redesocial = redesocial;
        this.password = password;
        this.uniqueCode = uniqueCode;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + idUser +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", evento=" + (evento != null ? evento.getIdEvento() : "null") +
                '}';
    }
}