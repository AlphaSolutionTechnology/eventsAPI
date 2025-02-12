package com.alphasolutions.eventapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "results")
@Getter
@Setter
@NoArgsConstructor
public class Results{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_result", nullable = false, updatable = false)
    private Long idResult;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "wrong_answers", nullable = false)
    private Integer wrongAnswers;

    @Column(name = "total_time", nullable = false)
    private Double totalTime;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id_user", nullable = false)
    private User user;

    public Results(Integer correctAnswers, Integer score, Integer wrongAnswers,Double totalTime,User user) {
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.wrongAnswers = wrongAnswers;
        this.totalTime = totalTime;
        this.user = user;
    }
    public Results(Long idResult, Integer correctAnswers, Integer score, Integer wrongAnswers,Double totalTime,User user) {
        this.idResult = idResult;
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.wrongAnswers = wrongAnswers;
        this.totalTime = totalTime;
        this.user = user;
    }
}
