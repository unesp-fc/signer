package br.unesp.fc.signer.web.recaptcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RecaptchaConfiguration implements WebMvcConfigurer {

    @Autowired
    private RecaptchaHandlerInterceptor recaptchaHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(recaptchaHandlerInterceptor);
    }

}
