package br.unesp.fc.signer.model;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.swing.table.AbstractTableModel;

public class KeyStoreTableModel extends AbstractTableModel {

    private final Map<String, X509Certificate> certificates = new TreeMap<>();
    private final List<String> aliases = new ArrayList<>();
    private final DateFormat dtf = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());

    public KeyStoreTableModel(KeyStore ks) throws KeyStoreException {
        for (var e = ks.aliases(); e.hasMoreElements();) {
            var alias = e.nextElement();
            if (!ks.isKeyEntry(alias)) {
                continue;
            }
            var certificate = ks.getCertificate(alias);
            if (certificate instanceof X509Certificate cert) {
                aliases.add(alias);
                certificates.put(alias, cert);
            }
        }
    }

    @Override
    public int getRowCount() {
        return certificates.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Nome";
            case 1:
                return "Começa em";
            case 2:
                return "Expira em";
            case 3:
                return "Válido";
            case 4:
                return "Serial";
        }
        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            var alias = aliases.get(rowIndex);
            var cert = certificates.get(alias);
            switch (columnIndex) {
                case 0:
                    var name = cert.getSubjectX500Principal().getName();
                    var ldapDN = new LdapName(name);
                    return ldapDN.getRdn(0).getValue();
                case 1:
                    return dtf.format(cert.getNotBefore());
                case 2:
                    return dtf.format(cert.getNotAfter());
                case 3:
                    try {
                        cert.checkValidity();
                        return cert.getKeyUsage()[0] ? "Sim" : "Não";
                    } catch (CertificateExpiredException | CertificateNotYetValidException ex) {
                        Logger.getLogger(KeyStoreTableModel.class.getName()).log(Level.SEVERE, null, ex);
                        return "Não";
                    }
                case 4:
                    return cert.getSerialNumber().toString(16);
            }
        } catch (InvalidNameException ex) {
            Logger.getLogger(KeyStoreTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String getValue(int rowIndex) {
        return aliases.get(rowIndex);
    }

}
