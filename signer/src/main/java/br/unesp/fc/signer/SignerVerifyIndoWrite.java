package br.unesp.fc.signer;

import br.unesp.fc.signer.model.PdfViewModel;
import br.unesp.fc.signer.model.SignVerifyInfoModel;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.COSFilterInputStream;
import org.apache.pdfbox.util.Matrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author demitrius
 */
@Component
public class SignerVerifyIndoWrite {

    private static final String TEXT = "Acesse https://sign.fc.unesp.br e digite o código  para validar a autenticidade.";
    private static final String CODE = "XXXX-XXXX-XXXX-XXXX";

    @Autowired
    private SignVerifyInfoModel signVerifyInfoModel;
    @Autowired
    private PdfViewModel pdfViewModel;
    @Autowired
    private SignatureService signatureService;

    public Rectangle2D.Float calcSize(float fontSize) throws IOException {
        PDFont textFont = new PDType1Font(signVerifyInfoModel.getTextFont());
        PDFont codeFont = new PDType1Font(signVerifyInfoModel.getCodeFont());
        var width = textFont.getStringWidth(TEXT) / 1000 * fontSize;
        width += codeFont.getStringWidth(CODE) / 1000 * fontSize;
        var height = textFont.getBoundingBox().getHeight() / 1000 * fontSize;
        return new Rectangle2D.Float(0, 0, width, height);
    }

    public COSStream write(PDDocument doc, PDPage page, boolean view) throws IOException {
        PDFont textFont = new PDType1Font(signVerifyInfoModel.getTextFont());
        PDFont codeFont = new PDType1Font(signVerifyInfoModel.getCodeFont());
        float fontSize = signVerifyInfoModel.getFontSize();
        var rect = signVerifyInfoModel.getRect();
        var size = calcSize(fontSize);
        int rotation = signVerifyInfoModel.getRotation();
        try (PDPageContentStream contents = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, false, true)) {
            if (!view) {
                var background = signVerifyInfoModel.getBackground();
                if (background != null && background.getAlpha() > 0) {
                    contents.setNonStrokingColor(background);
                    if (background.getAlpha() < 255) {
                        PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
                        graphicsState.setNonStrokingAlphaConstant(background.getAlpha() / 255f);
                        contents.setGraphicsStateParameters(graphicsState);
                    }
                    contents.addRect(rect.x, rect.y, rect.width, rect.height);
                    contents.fill();
                }
            }
            contents.beginText();
            float y = Math.abs((textFont.getFontDescriptor().getDescent() - textFont.getFontDescriptor().getLeading()) / 1000 * fontSize);
            y += (size.height - textFont.getFontDescriptor().getCapHeight() / 1000 * fontSize - y) / 2;
            if (view) {
                if (rotation == 0) {
                    contents.newLineAtOffset(0, y);
                } else if (rotation == 90) {
                    contents.setTextMatrix(Matrix.getRotateInstance(rotation * Math.PI / 180, size.height - y, 0));
                    contents.newLineAtOffset(0, 0);
                } else if (rotation == 180) {
                    contents.setTextMatrix(Matrix.getRotateInstance(rotation * Math.PI / 180, size.width, size.height - y));
                    contents.newLineAtOffset(0, 0);
                } else if (rotation == 270) {
                    contents.setTextMatrix(Matrix.getRotateInstance(rotation * Math.PI / 180, y, size.width));
                    contents.newLineAtOffset(0, 0);
                }
            } else {
                if (rotation == 0) {
                    contents.setTextMatrix(Matrix.getTranslateInstance(rect.x, rect.y));
                } else if (rotation == 90) {
                    contents.setTextMatrix(Matrix.getRotateInstance(rotation * Math.PI / 180, rect.width + rect.x, rect.y));
                } else if (rotation == 180) {
                    contents.setTextMatrix(Matrix.getRotateInstance(rotation * Math.PI / 180, rect.width + rect.x, rect.height + rect.y));
                } else if (rotation == 270) {
                    contents.setTextMatrix(Matrix.getRotateInstance(rotation * Math.PI / 180, rect.x, rect.height + rect.y));
                }
                if (rotation % 180 == 0) {
                    contents.newLineAtOffset((rect.width - size.width) / 2, (rect.height - size.height) / 2 + y);
                } else {
                    contents.newLineAtOffset((rect.height - size.width) / 2, (rect.width - size.height) / 2 + y);
                }
            }
            contents.setNonStrokingColor(signVerifyInfoModel.getForeground());
            contents.setFont(textFont, fontSize);
            contents.showText("Acesse ");
            contents.setNonStrokingColor(signVerifyInfoModel.getLinkColor());
            contents.showText("https://sign.fc.unesp.br");
            contents.setNonStrokingColor(signVerifyInfoModel.getForeground());
            contents.showText(" e digite o código ");
            contents.setFont(codeFont, fontSize);
            contents.showText("XXXX-XXXX-XXXX-XXXX");
            contents.setFont(textFont, fontSize);
            contents.showText(" para validar a autenticidade.");
            contents.endText();
        }
        var contents = page.getCOSObject().getDictionaryObject(COSName.CONTENTS);
        COSStream signInfo = null;
        if (contents instanceof COSArray array) {
            signInfo = (COSStream) array.get(array.size() - 1);
        } else if (contents instanceof COSStream stream) {
            signInfo = stream;
        }
        PDAnnotationLink link = new PDAnnotationLink();
        PDActionURI action = new PDActionURI();
        action.setURI("https://sign.fc.unesp.br");
        link.setAction(action);
        Color color = signVerifyInfoModel.getForeground();
        float [] components = new float [] { color.getRed () / 255f, color.getGreen () / 255f, color.getBlue () / 255f };
        link.setColor(new PDColor(components, PDDeviceRGB.INSTANCE));;
        if (!view) {
            if (rotation % 180 == 0) {
                link.setRectangle(new PDRectangle((rect.width - size.width) / 2 + rect.x, (rect.height - size.height) / 2 + rect.y, size.width, size.height));
            } else {
                link.setRectangle(new PDRectangle((rect.width - size.height) / 2 + rect.x, (rect.height - size.width) / 2 + rect.y, size.height, size.width));
            }
        }
        page.getAnnotations().add(link);
        return signInfo;
    }

    public File write(File pdf) throws IOException {
        RandomAccessRead raFile = new RandomAccessReadBufferedFile(pdf);
        var dir = pdf.toPath().getParent().resolve("info");
        dir.toFile().mkdirs();
        File newFile = dir.resolve(pdf.getName()).toFile();
        Set<COSBase> excludeFromUpdate = new HashSet<>();
        COSArray unespSign;
        try (PDDocument doc = Loader.loadPDF(raFile)) {
            PDPage page = doc.getPage(pdfViewModel.getCurrentPage());
            addRecursive(excludeFromUpdate, page.getCOSObject().getDictionaryObject(COSName.CONTENTS));
            COSStream signInfo = write(doc, page, false);
            var data = IOUtils.toByteArray(signInfo.createInputStream());
            int offset = search(CODE.getBytes(), data);
            var writer = new CustomCOSWriter(new FileOutputStream(newFile), raFile, excludeFromUpdate, signInfo, offset);
            writer.write(doc);
            unespSign = doc.getDocument().getTrailer().getCOSArray(COSName.getPDFName("UnespSign"));
        }
        var input = new FileInputStream(newFile);
        int[] byteRange = new int[] {0, unespSign.getInt(0), unespSign.getInt(0) + unespSign.getInt(1), (int) newFile.length() - (unespSign.getInt(0) + unespSign.getInt(1))};
        var filterInput = new COSFilterInputStream(input, byteRange);
        String code = signatureService.genCode(filterInput);
        var pdfInfo = new RandomAccessFile(newFile, "rw");
        pdfInfo.seek(unespSign.getInt(0));
        pdfInfo.write(code.getBytes());
        pdfInfo.close();
        return newFile;
    }

    private void addRecursive(Set<COSBase> excludeFromUpdate, COSBase contents) {
        if (!(contents instanceof COSArray)) {
            excludeFromUpdate.add(contents);
        }
    }

    private int search(byte[] pattern, byte[] data) {
        int matchCount = 0;
        for (int i = 0; i < data.length - pattern.length + 1; i++) {
            if (data[i] == pattern[matchCount]) {
                matchCount++;
            } else {
                matchCount = 0;
            }
            if (matchCount == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

}
