package br.unesp.fc.signer.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class FileModel {

    private PDDocument document;
    private PDFRenderer renderer;
    private File file;
    private BufferedImage image;

    public FileModel(File file) throws IOException {
        this.file = file;
        document = Loader.loadPDF(file);
        renderer = new PDFRenderer(document);
    }

    public PDDocument getDocument() {
        return document;
    }

    public void setDocument(PDDocument document) {
        this.document = document;
    }

    public PDFRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(PDFRenderer renderer) {
        this.renderer = renderer;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void close() throws IOException {
        document.close();
    }

}
