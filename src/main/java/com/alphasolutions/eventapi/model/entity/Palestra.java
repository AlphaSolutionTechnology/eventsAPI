package com.alphasolutions.eventapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Entity
@Table(name = "palestra")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Palestra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_palestra")
    private Long idPalestra;

    @Column(name = "tema", nullable = false, length = 80)
    private String tema;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_evento", nullable = false)
    private Evento evento;

    @Column(name = "unique_code", unique = true)
    private String uniqueCode;

    @Column(name = "quizz_liberado", nullable = false)
    private Boolean quizzLiberado = false;

    @Column(name = "hora_liberacao")
    private Timestamp horaLiberacao;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "palestrante", length = 255)
    private String palestrante;
}