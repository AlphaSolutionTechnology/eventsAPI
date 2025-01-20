package com.alphasolutions.eventapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "palestra")
@Data
public class Palestra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_palestra")
    private Long id;

    @Column(name = "tema", nullable = false, length = 80)
    private String tema;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id_user", nullable = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_evento", referencedColumnName = "id_evento", nullable = false)
    private Evento evento;
}
