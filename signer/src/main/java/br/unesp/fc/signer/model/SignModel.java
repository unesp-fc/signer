package br.unesp.fc.signer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import org.springframework.stereotype.Component;

@Component
public class SignModel {

    public static final String KEYSTORE = "keyStore";
    public static final String CERTIFICATE = "certificate";

    private KeyStore keyStore;
    private String alias;
    private X509Certificate certificate;
    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(KeyStore keyStore) {
        var old = this.keyStore;
        this.keyStore = keyStore;
        changes.fireIndexedPropertyChange(KEYSTORE, 0, old, keyStore);
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) throws KeyStoreException {
        this.alias = alias;
        this.certificate = (X509Certificate) keyStore.getCertificate(alias);
        changes.fireIndexedPropertyChange(CERTIFICATE, 0, null, certificate);
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public void addListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }

}
