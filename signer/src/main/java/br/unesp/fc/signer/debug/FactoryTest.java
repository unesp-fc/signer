package br.unesp.fc.signer.debug;

import br.unesp.fc.signer.model.PdfViewModel;
import br.unesp.fc.signer.model.SelectedFileModel;
import br.unesp.fc.signer.model.SignModel;
import br.unesp.fc.signer.model.SignVerifyInfoModel;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

//@Component
public class FactoryTest {

    @Bean
    public PdfViewModel pdfViewModel() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PdfViewModel.class);
        enhancer.setCallback(new Interceptor());
        return (PdfViewModel) enhancer.create();
    }

    @Bean
    public SelectedFileModel selectedFileModel() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SelectedFileModel.class);
        enhancer.setCallback(new Interceptor());
        return (SelectedFileModel) enhancer.create();
    }

    @Bean
    public SignVerifyInfoModel signVerifyInfoModel() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SignVerifyInfoModel.class);
        enhancer.setCallback(new Interceptor());
        return (SignVerifyInfoModel) enhancer.create();
    }

    @Bean
    public SignModel signModel() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SignModel.class);
        enhancer.setCallback(new Interceptor());
        return (SignModel) enhancer.create();
    }

    public static class Interceptor implements MethodInterceptor {

        private static final Logger log = LoggerFactory.getLogger(Interceptor.class);;

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            log.info("Class: {}, Method: {}, Args: {}", proxy.getClass().getSimpleName(), method.getName(), Arrays.toString(args));
            return proxy.invokeSuper(obj, args);
        }

    }

}
