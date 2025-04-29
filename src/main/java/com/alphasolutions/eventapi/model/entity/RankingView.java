package com.alphasolutions.eventapi.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

@Entity
@Table(name = "ranking_view")
@Immutable
@Data
public class RankingView {

    @Id
    @Column(name = "id_ranking")
    private Long idRanking;

    @Column(name = "acertos")
    private Integer acertos;

    @Column(name = "colocacao")
    private Integer colocacao;

    @Column(name = "conexoes")
    private Integer conexoes;

    @Column(name = "id_evento")
    private Long idEvento;

    @Column(name = "nome_usuario")
    private String nomeUsuario;

    @Column(name = "pontuacao_total")
    private Integer pontuacaoTotal;

    @Column(name = "id_user")
    private String idUser;
}