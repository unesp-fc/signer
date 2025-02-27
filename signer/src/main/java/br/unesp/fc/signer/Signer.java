package br.unesp.fc.signer;

import br.unesp.fc.signer.view.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;
import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.JFrame;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Signer {

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        setup();
        context = new SpringApplicationBuilder(Signer.class)
                .headless(false)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
        var child = new AnnotationConfigApplicationContext();
        child.setParent(context);
        child.refresh();
        java.awt.EventQueue.invokeLater(() -> {
            child.getBean(MainFrame.class).setVisible(true);
        });
    }

    public static void setup() {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        setupLaf();
    }

    public static void setupLaf() {
        FlatLightLaf.setup();
        if (FlatLightLaf.supportsNativeWindowDecorations()) {
            FlatLightLaf.setUseNativeWindowDecorations(true);
        } else {
            JFrame.setDefaultLookAndFeelDecorated(true);
        }
    }

    @Bean
    public BasicService basicService() throws UnavailableServiceException {
        return (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
    }

}
