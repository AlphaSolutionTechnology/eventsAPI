package com.alphasolutions.eventapi.model;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "ranking")
@Data
@Getter
@Setter
@NoArgsConstructor
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ranking")
    private Long id; 

    @ManyToOne
    @JoinColumn(name = "id_evento", referencedColumnName = "id_evento", nullable = true)
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "id_palestra", referencedColumnName = "id_palestra", nullable = false)
    private Palestra palestra;


    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id_user", nullable = false)
    private User user;

    @Column(name = "colocacao", nullable = false)
    private Integer colocacao;

    @Column(name = "conexoes", nullable = false)
    private Integer conexoes;

    @Column(name = "acertos", nullable = false)
    private Integer acertos;

    @Column(name = "pontuacao_total", nullable = false)
    private Integer pontuacaoTotal;

    public Ranking(Palestra palestra, User user) {
        this.palestra = palestra;
        this.user = user;
        setColocacao(0);
        setConexoes(0);
        setAcertos(0);
        setPontuacaoTotal(0);
    }

    public Ranking(Evento evento, User user){
        this.evento = evento;
        this.user = user;
        setColocacao(0);
        setConexoes(0);
        setAcertos(0);
        setPontuacaoTotal(0);
    }
}
