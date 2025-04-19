package com.alphasolutions.eventapi.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "evento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    @JsonProperty("title")
    private String nome;

    @Column(name = "descricao", nullable = true, length = 500)
    @JsonProperty("description")
    private String descricao;

    @Column(name = "data_evento", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("date")
    private Date dataEvento;

    @Column(name = "localizacao", nullable = false, length = 200)
    @JsonProperty("location")
    private String local;

    @Column(name = "imagem_url", nullable = false)
    @JsonProperty("image")
    private String imagemUrl;



}
