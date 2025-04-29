package com.alphasolutions.eventapi.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "questao_choices")
@Data
public class QuestaoChoices {

    @Id
    @Column(name = "id_questao")
    private Long idQuestao;

    @Column(name = "choice")
    private String choice;

    @ManyToOne
    @JoinColumn(name = "id_questao", insertable = false, updatable = false)
    private Questoes questao;
}