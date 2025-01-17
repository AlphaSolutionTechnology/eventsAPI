package com.alphasolutions.eventapi.model.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ranking")
@Data
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ranking")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_palestra", referencedColumnName = "id_palestra", nullable = true)
    private Palestra palestra;

    @Column(name = "colocacao", nullable = false)
    private Integer colocacao;

    @Column(name = "conexoes", nullable = false)
    private Integer conexoes;

    @Column(name = "acertos", nullable = false)
    private Integer acertos;
}
