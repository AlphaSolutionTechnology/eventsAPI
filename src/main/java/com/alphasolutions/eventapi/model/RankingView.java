package com.alphasolutions.eventapi.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "ranking_view")
@Getter
@Setter
public class RankingView {

    @Id
    @Column(name = "id_ranking")
    private Long idRanking; // ID da tabela ranking

    @Column(name = "id_user")
    private String idUser;

    @Column(name = "id_evento")
    private Long idEvento;

    @Column(name = "colocacao")
    private Integer colocacao;

    @Column(name = "conexoes")
    private Integer conexoes;

    @Column(name = "acertos")
    private Integer acertos;

    @Column(name = "pontuacao_total")
    private Integer pontuacaoTotal;

}
