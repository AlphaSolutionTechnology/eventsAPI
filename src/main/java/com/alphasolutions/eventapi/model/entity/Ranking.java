package com.alphasolutions.eventapi.model.entity;

import com.alphasolutions.eventapi.model.entity.Evento;
import com.alphasolutions.eventapi.model.entity.Palestra;
import com.alphasolutions.eventapi.model.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ranking")
@Data
@NoArgsConstructor
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ranking")
    private Long idRanking;

    @ManyToOne
    @JoinColumn(name = "id_palestra", nullable = false)
    private Palestra palestra;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(name = "conexoes", nullable = false)
    private Integer conexoes = 0;

    @Column(name = "acertos", nullable = false)
    private Integer acertos = 0;


    public Ranking(Palestra palestra, User user) {
        this.palestra = palestra;
        this.user = user;
    }


}