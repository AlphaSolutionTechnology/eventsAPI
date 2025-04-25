package com.alphasolutions.eventapi.model.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "conexao")
@Data
public class Conexao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conexao")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_solicitado", referencedColumnName = "id_user", nullable = false)
    private User solicitado;

    @ManyToOne
    @JoinColumn(name = "id_solicitante", referencedColumnName = "id_user", nullable = false)
    private User solicitante;

    @Column(name="status", nullable = false)
    private String status;

    public Conexao(User solicitante, User solicitado, String status) {
        this.solicitante = solicitante;
        this.solicitado = solicitado;
        this.status = status;
    }

    public Conexao() {

    }
}
