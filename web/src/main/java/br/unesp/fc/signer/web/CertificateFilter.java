package br.unesp.fc.signer.web;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class CertificateFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Forwarded-Tls-Client-Cert";
    private static final String ATTRIBUTE = "jakarta.servlet.request.X509Certificate";

    private CertificateFactory certificateFactory;

    public CertificateFilter() throws CertificateException {
        certificateFactory = CertificateFactory.getInstance("X.509");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getHeader(HEADER) == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            //var value = "-----BEGIN CERTIFICATE-----\n" + request.getHeader(HEADER) + "\n-----END CERTIFICATE-----";
            // TODO: Aceitar formato do Traefik
            var value = request.getHeader(HEADER).replace("\t", "\n");
            System.out.println();
            System.out.println(value);
            System.out.println();
            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(value.getBytes()));
            request.setAttribute(ATTRIBUTE, new X509Certificate[] {cert});
        } catch (Exception ex) {
            log.error(null, ex);
        }
        filterChain.doFilter(request, response);
    }

}
