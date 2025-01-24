package com.alphasolutions.eventapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "questoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Questoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_questao")
    private Long id;

    @Column(name = "enunciado", nullable = false, length = 255)
    private String enunciado;

    @ElementCollection
    @CollectionTable(name = "questao_choices", joinColumns = @JoinColumn(name = "id_questao"))
    @Column(name = "choice")
    private List<String> choices;

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @Column(name = "id_palestra", nullable = false)
    private Long idPalestra; // Adicionado o campo id_palestra
}
