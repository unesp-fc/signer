package br.unesp.fc.signer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfwriter.COSStandardOutputStream;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;

public class CustomCOSWriter extends COSWriter {

    private final Set<COSBase> excludedObjects;
    private final COSStream signInfo;
    private final int signInfoOffset;
    private final COSOutputStream output;
    private long signStreamPos = 0;
    private PDDocument doc;

    public CustomCOSWriter(OutputStream outputStream, RandomAccessRead inputData, Set<COSBase> excludedObjects, COSStream signInfo, int signInfoOffset) throws IOException {
        super(outputStream, inputData);
        this.excludedObjects = excludedObjects;
        this.signInfo = signInfo;
        this.signInfoOffset = signInfoOffset;
        output = new COSOutputStream(super.getStandardOutput());
    }

    @Override
    public void write(PDDocument doc, SignatureInterface signInterface) throws IOException {
        this.doc = doc;
        super.write(doc, signInterface);
    }

    @Override
    public void write(PDDocument doc) throws IOException {
        // Always incremental
        write(doc, null);
    }

    @Override
    public void doWriteObject(COSBase obj) throws IOException {
        if (!excludedObjects.contains(obj)) {
            if (signInfo == obj) {
                output.intercept = true;
            }
            super.doWriteObject(obj);
        }
    }

    private void setSignStreamPos(long pos) {
        signStreamPos = pos + signInfoOffset;
        var values = new COSArray();
        values.add(COSInteger.get(signStreamPos));
        values.add(COSInteger.get(19));
        doc.getDocument().getTrailer().setItem("UnespSign", values);
    }

    @Override
    protected COSStandardOutputStream getStandardOutput() {
        return output;
    }

    private class COSOutputStream extends COSStandardOutputStream {

        boolean intercept = false;

        public COSOutputStream(COSStandardOutputStream out) {
            super(out, out.getPos());
        }

        @Override
        public void write(byte[] b) throws IOException {
            if (intercept && b == STREAM) {
                setSignStreamPos(getPos() + STREAM.length + CRLF.length);
                intercept = false;
            }
            super.write(b);
        }

    }

}
