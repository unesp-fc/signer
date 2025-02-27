CREATE TABLE pessoa (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(64),
    cpf CHAR(11),
    PRIMARY KEY (id)
);

CREATE TABLE pdf (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(32),
    code CHAR(19),
    fileIdFist VARCHAR(32),
    fileIdLast VARCHAR(32),
    path VARCHAR(256),
    data DATETIME,
    idPessoa BIGINT,
    PRIMARY KEY(id),
    FOREIGN KEY (idPessoa) REFERENCES pessoa (id)
);
