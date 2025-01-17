package com.alphasolutions.eventapi.model.entities;
import jakarta.persistence.*;
import lombok.Data;

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
}
