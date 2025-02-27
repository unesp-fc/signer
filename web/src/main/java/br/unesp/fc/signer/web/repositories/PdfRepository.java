package br.unesp.fc.signer.web.repositories;

import br.unesp.fc.signer.web.entities.Pdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdfRepository extends JpaRepository<Pdf, Long>{

    public Pdf findByCode(String code);

}
