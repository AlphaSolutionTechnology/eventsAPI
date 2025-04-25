package com.alphasolutions.eventapi.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "conteudo")
@Data
public class Conteudo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conteudo")
    private Long id;

    @Column(name = "texto", nullable = false, length = 255)
    private String texto;

    @Column(name = "correta", nullable = false)
    private Boolean correta;

    @ManyToOne
    @JoinColumn(name = "id_questao", referencedColumnName = "id_questao", nullable = true)
    private Questoes questao;
}
