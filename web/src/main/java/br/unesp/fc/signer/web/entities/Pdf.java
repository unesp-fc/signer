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

    @Column
    private String nome;

    @Column(unique = true, updatable = false)
    private String code;

    @Column
    private String fileIdFist;

    @Column
    private String fileIdLast;

    @Column
    private String path;

    @Column
    private Timestamp data;

    @ManyToOne
    @JoinColumn(name = "idPessoa")
    private Pessoa pessoa;

}
