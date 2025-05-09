package br.unesp.fc.signer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFXRefStream;
import org.apache.pdfbox.pdfwriter.COSStandardOutputStream;
import org.apache.pdfbox.pdfwriter.COSWriter;
import static org.apache.pdfbox.pdfwriter.COSWriter.EOF;
import static org.apache.pdfbox.pdfwriter.COSWriter.STARTXREF;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.springframework.util.ReflectionUtils;

public class CustomCOSWriter extends COSWriter {

    private final Set<COSBase> excludedObjects;
    private final COSStream signInfo;
    private final int signInfoOffset;
    private final COSOutputStream output;
    private long signStreamPos = 0;
    private PDDocument doc;
    public static final COSName UNESP_SIGN = COSName.getPDFName("UnespSign");

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
        doc.getDocument().getTrailer().setItem(UNESP_SIGN, values);
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

    @Override
    public void visitFromDocument(COSDocument doc) throws IOException
    {
        // Sometimes the original file will be missing a newline at the end
        // In order to avoid having %%EOF the first object on the same line
        // as the %%EOF, we put a newline here. If there's already one at
        // the end of the file, an extra one won't hurt. PDFBOX-1051
        getStandardOutput().writeCRLF();

        doWriteBody(doc);

        if (!doc.isXRefStream() || doc.hasHybridXRef()) {
            //doWriteXRefInc(doc);
            var doWriteXRefInc = ReflectionUtils.findMethod(COSWriter.class, "doWriteXRefInc", COSDocument.class);
            ReflectionUtils.makeAccessible(doWriteXRefInc);
            ReflectionUtils.invokeMethod(doWriteXRefInc, this, doc);
        } else {
            doWriteXRefIncAlt(doc);
        }

        // write endof
        getStandardOutput().write(STARTXREF);
        getStandardOutput().writeEOL();
        getStandardOutput().write(String.valueOf(getStartxref()).getBytes(StandardCharsets.ISO_8859_1));
        getStandardOutput().writeEOL();
        getStandardOutput().write(EOF);
        getStandardOutput().writeEOL();

        var fieldSignatureOffset = ReflectionUtils.findField(COSWriter.class, "signatureOffset");
        ReflectionUtils.makeAccessible(fieldSignatureOffset);
        var signatureOffset = (long) ReflectionUtils.getField(fieldSignatureOffset, this);
        var fieldByteRangeOffset = ReflectionUtils.findField(COSWriter.class, "byteRangeOffset");
        ReflectionUtils.makeAccessible(fieldByteRangeOffset);
        var byteRangeOffset = (long) ReflectionUtils.getField(fieldByteRangeOffset, this);

        if (signatureOffset == 0 || byteRangeOffset == 0)
        {
            //doWriteIncrement();
            var doWriteIncrement = ReflectionUtils.findMethod(COSWriter.class, "doWriteIncrement");
            ReflectionUtils.makeAccessible(doWriteIncrement);
            ReflectionUtils.invokeMethod(doWriteIncrement, this);
        }
        else
        {
            //doWriteSignature();
            var doWriteSignature = ReflectionUtils.findMethod(COSWriter.class, "doWriteSignature");
            ReflectionUtils.makeAccessible(doWriteSignature);
            ReflectionUtils.invokeMethod(doWriteSignature, this);
        }

    }

    private void doWriteXRefIncAlt(COSDocument doc) throws IOException
    {
        var fieldNumber = ReflectionUtils.findField(COSWriter.class, "number");
        ReflectionUtils.makeAccessible(fieldNumber);
        var number = (long) ReflectionUtils.getField(fieldNumber, this);

        // the file uses XrefStreams, so we need to update
        // it with an xref stream. We create a new one and fill it
        // with data available here

        // create a new XRefStream object
        PDFXRefStream pdfxRefStream = new CustomPDFXRefStream(doc);

        // add all entries from the incremental update.
        getXRefEntries().forEach(pdfxRefStream::addEntry);

        COSDictionary trailer = doc.getTrailer();

        // use previous startXref value as new PREV value
        trailer.setLong(COSName.PREV, doc.getStartXref());

        pdfxRefStream.addTrailerInfo(trailer);
        // the size is the highest object number+1. we add one more
        // for the xref stream object we are going to write
        pdfxRefStream.setSize(number + 2);

        setStartxref(getStandardOutput().getPos());
        COSStream stream2 = pdfxRefStream.getStream();
        doWriteObject(stream2);
    }

    public class CustomPDFXRefStream extends PDFXRefStream {

        private final COSStream stream;

        public CustomPDFXRefStream(COSDocument cosDocument) {
            super(cosDocument);
            var fieldStream = ReflectionUtils.findField(PDFXRefStream.class, "stream");
            ReflectionUtils.makeAccessible(fieldStream);
            stream = (COSStream) ReflectionUtils.getField(fieldStream, this);
        }

        @Override
        public void addTrailerInfo(COSDictionary trailerDict) {
            trailerDict.forEach((key, value) ->
            {
                if (COSName.INFO.equals(key) || COSName.ROOT.equals(key) || COSName.ENCRYPT.equals(key)
                        || COSName.ID.equals(key) || COSName.PREV.equals(key) || UNESP_SIGN.equals(key)) {
                    stream.setItem(key, value);
                }
            });
        }

    }

}
