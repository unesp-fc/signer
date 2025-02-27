package br.unesp.fc.signer.local;

import java.net.URL;

/**
 *
 * @author demitrius
 */
public class BasicService implements javax.jnlp.BasicService {

    private URL codeBase;

    public BasicService(URL codeBase) {
        this.codeBase = codeBase;
    }

    @Override
    public URL getCodeBase() {
        return codeBase;
    }

    @Override
    public boolean isOffline() {
        return false;
    }

    @Override
    public boolean showDocument(URL url) {
        return false;
    }

    @Override
    public boolean isWebBrowserSupported() {
        return false;
    }

}
