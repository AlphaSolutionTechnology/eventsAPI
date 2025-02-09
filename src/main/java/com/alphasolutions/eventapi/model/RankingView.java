package com.alphasolutions.eventapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ranking_view")
@Getter
@Setter
@NoArgsConstructor
public class RankingView {

    @Id
    @Column(name = "id_ranking")
    private Long id;

    @Column(name = "id_user")
    private String userId;

    @Column(name = "nome_usuario")
    private String nomeUsuario;

    @Column(name = "id_evento")
    private Long eventoId;

    @Column(name = "colocacao")
    private Integer colocacao;

    @Column(name = "conexoes")
    private Integer conexoes;

    @Column(name = "acertos")
    private Integer acertos;

    @Column(name = "pontuacao_total")
    private Integer pontuacaoTotal;
}
