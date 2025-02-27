package br.unesp.fc.signer.web;

import br.unesp.fc.signer.web.entities.Pdf;
import br.unesp.fc.signer.web.entities.Pessoa;
import br.unesp.fc.signer.web.repositories.PdfRepository;
import br.unesp.fc.signer.web.repositories.PessoaRepository;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
public class Service {

    private final Path pdfDir;
    private final PdfRepository pdfRepository;
    private final PessoaRepository pessoaRepository;

    public Service(@Value("${pdf.dir}") String pdfDir, PdfRepository pdfRepository, PessoaRepository pessoaRepository) {
        this.pdfDir = Paths.get(pdfDir);
        this.pdfRepository = pdfRepository;
        this.pessoaRepository = pessoaRepository;
    }

    @Transactional
    public void save(Pdf pdf, File file) throws FileNotFoundException, IOException {
        int year = Year.now().getValue();
        Path dir = pdfDir.resolve(String.valueOf(year));
        Files.createDirectories(dir);
        Path path = dir.resolve(pdf.getCode() + ".pdf");
        var newFile = path.toFile();
        if (newFile.exists()) {
            throw new RuntimeException("Arquivo já existente ou código repetido");
        }
        Files.copy(file.toPath(), new FileOutputStream(newFile));
        pdf.setPath(pdfDir.relativize(path).toString());
        pdfRepository.save(pdf);
    }

    public Path getPdfPath(Pdf pdf) {
        return pdfDir.resolve(pdf.getPath());
    }

    @Transactional(readOnly = true)
    public Pdf getPdf(String code) {
        code = code.toUpperCase();
        code = code
                .replace("0", "O")
                .replace("1", "I")
                .replace("8", "B");
        return pdfRepository.findByCode(code);
    }

    @Transactional
    public Pessoa getPessoa(String name) {
        int i = name.indexOf(':');
        if (i < 0) {
            return null;
        }
        String cpf = name.substring(i + 1);
        Pessoa pessoa = pessoaRepository.findByCpf(cpf);
        if (pessoa != null) {
            return pessoa;
        }
        pessoa = new Pessoa();
        pessoa.setCpf(cpf);
        pessoa.setNome(name.substring(0, i));
        return pessoaRepository.save(pessoa);
    }

}
