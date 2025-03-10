package com.alphasolutions.eventapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Results {

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

    @Column(name = "total_time")
    private Double totalTime;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_palestra", referencedColumnName = "id_palestra", nullable = false)
    private Palestra palestra;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    public Results(Integer correctAnswers, Integer score, Integer wrongAnswers, User user, Palestra palestra) {
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.wrongAnswers = wrongAnswers;
        this.user = user;
        this.palestra = palestra;
        this.startTime = LocalDateTime.now();
    }


    public Results(Integer correctAnswers, Integer score, Integer wrongAnswers, Double totalTime, User user) {
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.wrongAnswers = wrongAnswers;
        this.totalTime = totalTime;
        this.user = user;
        this.startTime = LocalDateTime.now();
    }
}