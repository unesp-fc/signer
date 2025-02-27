package br.unesp.fc.signer.web;

import br.unesp.fc.signer.SignatureService;
import br.unesp.fc.signer.web.entities.Pdf;
import br.unesp.fc.signer.web.recaptcha.RecaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
public class Controller {

    private final Service service;
    private final SignatureService signatureService;
    private final RecaptchaService recaptchaService;

    @PostMapping("/upload")
    public ResponseEntity upload(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Usuario usuario) throws IOException {
        if (!MediaType.APPLICATION_PDF.includes(MediaType.valueOf(file.getContentType()))) {
            return new ResponseEntity("Arquivo inválido!", HttpStatus.BAD_REQUEST);
        }
        File f = File.createTempFile("sign-", ".pdf");
        file.transferTo(f);
        String code;
        String[] ids;
        if ((code = signatureService.validadeCode(f)) == null) {
            return new ResponseEntity("Arquivo inválido!", HttpStatus.BAD_REQUEST);
        }
        if ((ids = signatureService.getFileId(f)) == null) {
            return new ResponseEntity("Arquivo inválido!", HttpStatus.BAD_REQUEST);
        }
        if (!signatureService.validadeSign(f)) {
            return new ResponseEntity("Arquivo inválido!", HttpStatus.BAD_REQUEST);
        }
        Pdf pdf = new Pdf();
        pdf.setNome(file.getOriginalFilename());
        pdf.setCode(code);
        pdf.setFileIdFist(ids[0]);
        pdf.setFileIdLast(ids[1]);
        pdf.setData(Timestamp.valueOf(LocalDateTime.now()));
        pdf.setPessoa(usuario.getPessoa());
        service.save(pdf, f);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/")
    public String post(@RequestParam("code") String code, @RequestParam("g-recaptcha-response") String recaptchaResponse, HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        if (!recaptchaService.verify(recaptchaResponse, request.getServerName())) {
            model.addAttribute("error", "Erro validando captcha.");
            return "index";
        }
        var pdf = service.getPdf(code);
        if (pdf == null) {
            model.addAttribute("error", "PDF não encontrado!");
            return "index";
        }
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        var path = service.getPdfPath(pdf);
        response.setContentLengthLong(path.toFile().length());
        Files.copy(path, response.getOutputStream());
        return null;
    }

}
