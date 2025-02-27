package br.unesp.fc.signer;

import br.unesp.fc.signer.local.BasicService;
import br.unesp.fc.signer.local.ServiceManagerStub;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

public class Local {

    public static void main(String args[]) throws MalformedURLException, UnavailableServiceException, URISyntaxException, InterruptedException {
        // Init
        ServiceManagerStub.add("javax.jnlp.BasicService", new BasicService(new URL("https://sign.fc.unesp.br/webstart/")));
//        ServiceManagerStub.add("javax.jnlp.PersistenceService", new PersistenceService());
        ServiceManager.setServiceManagerStub(new ServiceManagerStub());
        Signer.main(args);
    }

}
