package com.alphasolutions.eventapi.model.entities;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "\"user\"")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "id_role", referencedColumnName = "id_role", nullable = true)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "id_evento", referencedColumnName = "id_evento", nullable = true)
    private Evento evento;
    @Column(name = "email",nullable = false, length = 255)
    private String email;
    @Column(name = "redesocial", length = 80)
    private String redeSocial;
    public User() {}
    public User(String email,String nome, Role role, Evento evento) {

    }
}
