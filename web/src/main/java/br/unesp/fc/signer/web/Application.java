package br.unesp.fc.signer.web;

import java.security.cert.CertificateException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
@ComponentScan(basePackages = "br.unesp.fc.signer")
@EnableWebSecurity
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((c) -> {
                    c
                            .requestMatchers("/upload").authenticated()
                            .requestMatchers("/teste").authenticated()
                            .anyRequest().anonymous();
                })
                .x509((c) -> {
                })
                .csrf(c -> {
                    c.ignoringRequestMatchers("/upload");
                });
        return http.build();
    }

    @Bean
    public FilterRegistrationBean certificatefilter() throws CertificateException {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new CertificateFilter());
        registrationBean.setOrder(HIGHEST_PRECEDENCE);
        return registrationBean;
    }

}
