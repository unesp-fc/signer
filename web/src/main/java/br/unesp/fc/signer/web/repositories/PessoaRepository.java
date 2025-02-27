package br.unesp.fc.signer.web.repositories;

import br.unesp.fc.signer.web.entities.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long>{

    Pessoa findByCpf(String cpf);

}
