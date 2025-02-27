package br.unesp.fc.signer.webstart;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/webstart/**")
public class WebstartController {

    private static final Logger log = LoggerFactory.getLogger(WebstartController.class);

//    private final JnlpDownloadServlet jnlpDownloadServlet = new JnlpDownloadServlet();

//    @PostConstruct
//    public void init() {
//        try {
//            jnlpDownloadServlet.init();
//        } catch (ServletException ex) {
//            log.error(null, ex);
//        }
//    }

//    @RequestMapping
//    public void execute(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//        boolean isHead = req.getMethod().equalsIgnoreCase("HEAD");
//        if (isHead) {
//            jnlpDownloadServlet.doHead(req, res);
//        } else {
//            jnlpDownloadServlet.doGet(req, res);
//        }
//    }

    @RequestMapping
    public void execute(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getServletContext().getNamedDispatcher("jnlpDownloadServlet").forward(req, res);
    }

}
