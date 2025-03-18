package br.unesp.fc.signer;

import br.unesp.fc.signer.model.FileListModel;
import br.unesp.fc.signer.model.SignModel;
import br.unesp.fc.signer.view.MainFrame;
import br.unesp.fc.signer.view.PasswordDialog;
import br.unesp.fc.signer.view.ProgressDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jnlp.BasicService;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.net.ssl.SSLContext;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.examples.signature.CMSProcessableInputStream;
import org.apache.pdfbox.examples.signature.SigUtils;
import org.apache.pdfbox.examples.signature.ValidationTimeStamp;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SignerWork {

    @Autowired
    private SignModel signModel;

    @Autowired
    private MainFrame main;

    @Autowired
    private SignerVerifyIndoWrite signerVerifyIndoWrite;

    @Autowired
    private FileListModel fileListModel;

    private URI uploadUrl;

    private ExecutorService executor =  Executors.newSingleThreadExecutor();

    @Autowired
    private void init(BasicService basicService) throws URISyntaxException {
        uploadUrl = basicService.getCodeBase().toURI().resolve("..").resolve("upload");
    }

    public void run() throws KeyStoreException {
        char[] password = new char[] {};
        PrivateKey key = null;
        do {
            try {
                key = (PrivateKey) signModel.getKeyStore().getKey(signModel.getAlias(), password);
            } catch (UnrecoverableKeyException ex) {
                if (password.length != 0) {
                    JOptionPane.showMessageDialog(main, "Senha inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
                var passDialog = new PasswordDialog(main, true);
                passDialog.setLocation(main.getX() + (main.getWidth() - passDialog.getWidth()) / 2, main.getY() + (main.getHeight() - passDialog.getHeight()) / 2);
                passDialog.setVisible(true);
                if (passDialog.getPassword() == null) {
                    return;
                }
                password = passDialog.getPassword();
            } catch (NoSuchAlgorithmException | KeyStoreException ex) {
                Logger.getLogger(SignerWork.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(main, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } while (key == null);
        X509Certificate certificate = (X509Certificate) signModel.getKeyStore().getCertificate(signModel.getAlias());
        Certificate[] chain = readChain();
        chain[0] = certificate;
        SignerWork.Signer signer = new SignerWork.Signer(certificate, key, chain);
        CloseableHttpClient httpClient;
        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadKeyMaterial(signModel.getKeyStore(), password, (aliases, sslParameters) -> signModel.getAlias())
                    .build();
            SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                .build();
            HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();
            httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .evictExpiredConnections()
                .build();
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyManagementException ex) {
            Logger.getLogger(SignerWork.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(main, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        executor.execute(() -> {
            ProgressDialog progressDialog = new ProgressDialog(main, true);
            progressDialog.setNumFiles(fileListModel.getSize());
            progressDialog.setValue(0);
            progressDialog.setLocation(main.getX() + (main.getWidth() - progressDialog.getWidth()) / 2, main.getY() + (main.getHeight() - progressDialog.getHeight()) / 2);
            SwingUtilities.invokeLater(() -> progressDialog.setVisible(true));
            try {
                for (int i = 0; i < fileListModel.getSize(); i++) {
                    try {
                        var fileModel = fileListModel.getElementAt(i);
                        SwingUtilities.invokeLater(() -> progressDialog.setMessage("Assinando " + fileModel.getFile().getName() + " ..."));
                        var file = signPdf(fileModel.getFile(), signer);
                        send(httpClient, fileModel.getFile(), file);
                        final int value = i + 1;
                        SwingUtilities.invokeLater(() -> progressDialog.setValue(value));
                    } catch (Exception ex) {
                        Logger.getLogger(SignerWork.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(main, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            } finally {
                SwingUtilities.invokeLater(() -> progressDialog.dispose());
                try {
                    httpClient.close();
                } catch (IOException ex) {
                    Logger.getLogger(SignerWork.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            JOptionPane.showMessageDialog(main, "Assinatura concluída!", "Assinatura", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private static Certificate[] readChain() {
        try {
            return new Certificate[] {
                null,
                CertificateFactory.getInstance("X.509").generateCertificate(SignerWork.class.getResourceAsStream("/ac-pessoa.cer")),
                CertificateFactory.getInstance("X.509").generateCertificate(SignerWork.class.getResourceAsStream("/ac-raiz-icpedu-v3.crt")),
            };
        } catch (CertificateException ex) {
            Logger.getLogger(SignerWork.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private File signPdf(File pdf, Signer signer) throws IOException {
        File file = signerVerifyIndoWrite.write(pdf);
        PDDocument document = Loader.loadPDF(file);
        SigUtils.checkCrossReferenceTable(document);
        int accessPermissions = SigUtils.getMDPPermission(document);
        if (accessPermissions == 1) {
            throw new IllegalStateException("No changes to the document are permitted due to DocMDP transform parameters dictionary");
        }

        PDSignature signature = new PDSignature();
        signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
        signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
        signature.setName(signer.getName());
        signature.setLocation(signer.getLocation());
        signature.setSignDate(Calendar.getInstance());

        if (accessPermissions == 0) {
            SigUtils.setMDPPermission(document, signature, 2);
        }

        SignatureOptions signatureOptions = new SignatureOptions();
        signatureOptions.setPreferredSignatureSize(SignatureOptions.DEFAULT_SIGNATURE_SIZE * 2);
        document.addSignature(signature, signer, signatureOptions);

        var dir = file.toPath().getParent().getParent().resolve("assinado");
        dir.toFile().mkdirs();
        File newFile = dir.resolve(pdf.getName()).toFile();
        document.saveIncremental(new FileOutputStream(newFile));
        return newFile;
    }

    private class Signer implements SignatureInterface {

        private final X509Certificate cert;
        private final PrivateKey privateKey;
        private Certificate[] certificateChain;
        private String tsaUrl = "http://timestamp.digicert.com";

        public Signer(X509Certificate cert, PrivateKey privateKey, Certificate[] certificateChain) {
            this.cert = cert;
            this.privateKey = privateKey;
            this.certificateChain = certificateChain;
        }

        @Override
        public byte[] sign(InputStream in) throws IOException {
            try {
                CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
                ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA256WithRSA").build(privateKey);
                gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().build()).build(sha1Signer, cert));
                gen.addCertificates(new JcaCertStore(Arrays.asList(certificateChain)));
                CMSProcessableInputStream msg = new CMSProcessableInputStream(in);
                CMSSignedData signedData = gen.generate(msg, false);
                if (tsaUrl != null && tsaUrl.length() > 0) {
                    ValidationTimeStamp validation = new ValidationTimeStamp(tsaUrl);
                    signedData = validation.addSignedTimeStamp(signedData);
                }
                return signedData.getEncoded();
            } catch (OperatorCreationException | CertificateEncodingException | CMSException | NoSuchAlgorithmException | MalformedURLException | URISyntaxException ex) {
                throw new IOException(ex);
            }
        }

        public String getName() {
            try {
                var name = cert.getSubjectX500Principal().getName();
                var ldapDN = new LdapName(name);
                String cn = (String) ldapDN.getRdn(0).getValue();
                int indexOf = cn.indexOf(":");
                if (indexOf >= 0) {
                    return cn.substring(0, indexOf);
                }
                return cn;
            } catch (InvalidNameException ex) {
                Logger.getLogger(SignerWork.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        public String getLocation() {
            try {
                var name = cert.getSubjectX500Principal().getName();
                var ldapDN = new LdapName(name);
                String cn = (String) ldapDN.getRdn(1).getValue();
                return cn;
            } catch (InvalidNameException ex) {
                Logger.getLogger(SignerWork.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

    }

    public void send(CloseableHttpClient httpClient, File orig, File assinado) throws IOException {
        var entity = MultipartEntityBuilder.create()
                .addBinaryBody("file", assinado, ContentType.APPLICATION_PDF, orig.getName())
                .build();
        var httpPost = ClassicRequestBuilder
                .post(uploadUrl)
                .setEntity(entity)
                .build();
        httpClient.execute(httpPost, (response) -> {
            if (response.getCode() != 200) {
                throw new RuntimeException("Servidor retornou erro " + response.getCode() + "!");
            }
            return null;
        });
    }

}
