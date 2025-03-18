package br.unesp.fc.signer.web.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.sql.Timestamp;
import lombok.Data;

@Entity
@Data
public class Pdf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(updatable = false, nullable = false)
    private String code;

    @Column(nullable = false)
    private String fileIdFist;

    @Column(nullable = false)
    private String fileIdLast;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private Timestamp data;

    @ManyToOne()
    @JoinColumn(name = "id_pessoa", nullable = false)
    private Pessoa pessoa;

    @ManyToOne
    @JoinColumn(name = "id_finalidade", nullable = false)
    private Finalidade finalidade;

}
