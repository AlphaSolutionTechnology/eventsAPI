package com.alphasolutions.eventapi.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "evento")
@Data
@NoArgsConstructor
@Setter
@Getter
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long idEvento;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "imagem_url")
    private String imagemUrl;

    @Column(name = "data_evento")
    private LocalDate dataEvento;

    @Column(name = "localizacao", length = 100)
    private String localizacao;

    public Evento(long l, String primeiroEvento) {
        this.idEvento = l;
        this.nome = primeiroEvento;
    }
}