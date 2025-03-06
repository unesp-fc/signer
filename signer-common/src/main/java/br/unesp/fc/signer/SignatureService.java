package br.unesp.fc.signer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.examples.signature.SigUtils;
import org.apache.pdfbox.examples.signature.cert.CertificateVerificationException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.COSFilterInputStream;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base32;
import org.springframework.stereotype.Service;

@Service
public class SignatureService {

    private final byte[] EOF = "\n%%EOF\n".getBytes();

    public String validadeCode(File pdf) throws IOException {
        COSArray unespSign;
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            unespSign = doc.getDocument().getTrailer().getCOSArray(COSName.getPDFName("UnespSign"));
        }
        if (unespSign == null) {
            return null;
        }
        var input = new FileInputStream(pdf);
        var pdfInfo = new RandomAccessFile(pdf, "rw");
        var end = findEof(pdfInfo, unespSign.getInt(0));
        int[] byteRange = new int[] {0, unespSign.getInt(0), unespSign.getInt(0) + unespSign.getInt(1), end - (unespSign.getInt(0) + unespSign.getInt(1))};
        var filterInput = new COSFilterInputStream(input, byteRange);
        String code = genCode(filterInput);
        pdfInfo.seek(unespSign.getInt(0));
        byte[] b = new byte[code.getBytes().length];
        pdfInfo.readFully(b);
        pdfInfo.close();
        if (!code.equals(new String(b))) {
            return null;
        }
        return code;
    }

    public String genCode(COSFilterInputStream filterInput) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            DigestInputStream dis = new DigestInputStream(filterInput, md);
            dis.transferTo(OutputStream.nullOutputStream());
            var digest = md.digest();
            var code = Base32.toBase32String(digest);
            code = code.substring(0, 4) + "-" + code.substring(4, 8)
                    + "-" + code.substring(8, 12) + "-" + code.substring(12, 16);
            return code;
        } catch (NoSuchAlgorithmException ex) {
        }
        return "";
    }

    public boolean validadeSign(File pdf) throws IOException {
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            var signatureDictionaries = doc.getSignatureDictionaries();
            for (var signatureDictionary : signatureDictionaries) {
                byte[] signatureContent = signatureDictionary.getContents();
                byte[] signedContent = signatureDictionary.getSignedContent(new FileInputStream(pdf));
                CMSSignedData signedData = new CMSSignedData(new CMSProcessableByteArray(signedContent), signatureContent);
                Store<X509CertificateHolder> certificatesStore = signedData.getCertificates();
                Collection<SignerInformation> signers = signedData.getSignerInfos().getSigners();
                SignerInformation signerInformation = signers.iterator().next();
                Collection<X509CertificateHolder> matches = certificatesStore
                        .getMatches((Selector<X509CertificateHolder>) signerInformation.getSID());
                X509CertificateHolder certificateHolder = matches.iterator().next();
                if (!signerInformation.verify(new JcaSimpleSignerInfoVerifierBuilder().build(certificateHolder))) {
                    return false;
                }
                TimeStampToken timeStampToken = SigUtils.extractTimeStampTokenFromSignerInformation(signerInformation);
                SigUtils.validateTimestampToken(timeStampToken);
                TimeStampTokenInfo timeStampInfo = timeStampToken.getTimeStampInfo();
                byte[] tsMessageImprintDigest = timeStampInfo.getMessageImprintDigest();
                String hashAlgorithm = timeStampInfo.getMessageImprintAlgOID().getId();
                byte[] sigMessageImprintDigest = MessageDigest.getInstance(hashAlgorithm).digest(signerInformation.getSignature());
                if (!Arrays.equals(sigMessageImprintDigest, tsMessageImprintDigest)) {
                    return false;
                }
                Store<X509CertificateHolder> tsCertStore = timeStampToken.getCertificates();
                Collection<X509CertificateHolder> tsCertStoreMatches = tsCertStore.getMatches(timeStampToken.getSID());
                X509CertificateHolder certHolderFromTimeStamp = tsCertStoreMatches.iterator().next();
                X509Certificate certFromTimeStamp = new JcaX509CertificateConverter().getCertificate(certHolderFromTimeStamp);
                SigUtils.checkTimeStampCertificateUsage(certFromTimeStamp);
                SigUtils.verifyCertificateChain(tsCertStore, certFromTimeStamp, timeStampInfo.getGenTime());
                certificateHolder.getSubject();
            }
        } catch (CMSException | OperatorCreationException | CertificateException | TSPException | NoSuchAlgorithmException | CertificateVerificationException ex) {
            Logger.getLogger(SignatureService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public String[] getFileId(File pdf) throws IOException {
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            COSArray ids = doc.getDocument().getDocumentID();
            if (ids.size() == 2) {
                return new String[] {
                    ((COSString)ids.getObject(0)).toHexString(),
                    ((COSString)ids.getObject(1)).toHexString(),
                };
            }
        }
        return null;
    }

    private int findEof(RandomAccessFile pdfInfo, int offset) throws IOException {
        pdfInfo.seek(offset);
        int r;
        int count = 0;
        while ((r = pdfInfo.read()) != -1) {
            byte b = (byte) (r & 0xff);
            if (b == EOF[count]) {
                count++;
            }
            if (count == EOF.length) {
                return (int) pdfInfo.getFilePointer();
            }
        }
        return 0;
    }

    public static void main(String args[]) throws IOException {
        System.out.println(new SignatureService().validadeCode(new File("/home/demitrius/sample-assinado.pdf")));
        System.out.println(new SignatureService().validadeCode(new File("/home/demitrius/sample-info.pdf")));
    }

}
