CREATE TABLE pessoa (
    id bigint NOT NULL AUTO_INCREMENT,
    cpf char(11) NOT NULL,
    nome varchar(127) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (cpf)
) ENGINE = InnoDB;

CREATE TABLE finalidade (
    id bigint NOT NULL AUTO_INCREMENT,
    nome varchar(128) NOT NULL,
    data date NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE pdf (
    id bigint NOT NULL AUTO_INCREMENT,
    id_pessoa bigint NOT NULL,
    id_finalidade bigint NOT NULL,
    nome varchar(127) NOT NULL,
    path varchar(256) NOT NULL,
    code varchar(32) NOT NULL,
    data datetime NOT NULL,
    file_id_fist varchar(64) DEFAULT NULL,
    file_id_last varchar(64) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (code),
    KEY pdf_pessoa (id_pessoa),
    FOREIGN KEY pdf_pessoa (id_pessoa) REFERENCES pessoa (id),
    KEY pdf_finalidade (id_finalidade),
    FOREIGN KEY pdf_finalidade (id_finalidade) REFERENCES finalidade (id)
) ENGINE = InnoDB;
