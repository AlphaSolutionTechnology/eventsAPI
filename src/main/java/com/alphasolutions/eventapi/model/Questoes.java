package com.alphasolutions.eventapi.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "questoes")
@Data
public class Questoes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_questao")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id_user", nullable = true)
    private User user;

    @Column(name = "enunciado", nullable = false, length = 255)
    private String enunciado;

    @ManyToOne
    @JoinColumn(name = "id_palestra", referencedColumnName = "id_palestra", nullable = false)
    private Palestra palestra;
}
