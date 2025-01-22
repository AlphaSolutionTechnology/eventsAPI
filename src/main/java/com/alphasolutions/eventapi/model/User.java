package com.alphasolutions.eventapi.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.persistence.SequenceGenerator;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"user\"")
@NoArgsConstructor
@AllArgsConstructor
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
    private String uniqueCode;

}
