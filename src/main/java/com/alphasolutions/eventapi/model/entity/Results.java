package com.alphasolutions.eventapi.model.entity;

import com.alphasolutions.eventapi.model.entity.Palestra;
import com.alphasolutions.eventapi.model.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Data
@NoArgsConstructor
public class Results {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_result")
    private Long idResult;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;

    @Column(name = "wrong_answers", nullable = false)
    private Integer wrongAnswers;

    @Column(name = "total_time")
    private Double totalTime;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_palestra", nullable = false)
    private Palestra palestra;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "score", nullable = false)
    private Integer score;

    public Results(int correctAnswerCount, int score, int wrongAnswerCount, User user, Palestra palestra) {
        this.correctAnswers = correctAnswerCount;
        this.score = score;
        this.wrongAnswers = wrongAnswerCount;
        this.user = user;
        this.palestra = palestra;
        this.startTime = LocalDateTime.now();
        this.endTime = LocalDateTime.now();
    }
}