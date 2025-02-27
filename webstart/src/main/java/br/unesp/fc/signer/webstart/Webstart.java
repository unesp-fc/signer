package br.unesp.fc.signer.webstart;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Webstart extends SpringBootServletInitializer {

    @Bean
    public ServletRegistrationBean JnlpDownloadServlet() {
        var servletRegistrationBean = new ServletRegistrationBean(new JnlpDownloadServlet(), "/webstart/*");
        servletRegistrationBean.setName("jnlpDownloadServlet");
        return servletRegistrationBean;
    }

}
