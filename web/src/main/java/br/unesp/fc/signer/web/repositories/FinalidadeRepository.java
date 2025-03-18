package br.unesp.fc.signer.web.repositories;

import br.unesp.fc.signer.web.entities.Finalidade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinalidadeRepository extends JpaRepository<Finalidade, Long> {

}
