package com.alphasolutions.eventapi.model;


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

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id_user", nullable = false)
    private User user;

    @Column(name = "colocacao", nullable = false)
    private Integer colocacao;

    @Column(name = "conexoes", nullable = false)
    private Integer conexoes;

    @Column(name = "acertos", nullable = false)
    private Integer acertos;

    @Column(name = "pontuacao_total", nullable = false)
    private Integer pontuacaoTotal;
}
