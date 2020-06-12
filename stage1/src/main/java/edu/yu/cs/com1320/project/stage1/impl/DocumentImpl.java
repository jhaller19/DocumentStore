package edu.yu.cs.com1320.project.stage1.impl;

import edu.yu.cs.com1320.project.stage1.Document;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

public class DocumentImpl implements Document {

    private int textHashCode;
    private URI uri;
    private String text;
    private byte[] pdfBytes;

    //Constructor for TXT
    public DocumentImpl(URI uri, String text, int textHashCode){
        if(uri == null || text == null){
            throw new IllegalArgumentException("URI or text cannot be null");
        }
        this.uri = uri;
        this.text = text;
        this.textHashCode = textHashCode;
    }

    //Constructor for PDF
    public DocumentImpl(URI uri, String text, int textHashCode, byte[] pdfBytes){
        if(uri == null || text == null || pdfBytes == null){
            throw new IllegalArgumentException("URI or text or pdf bytes cannot be null");
        }
        this.uri = uri;
        this.text = text;
        this.textHashCode = textHashCode;
        this.pdfBytes = pdfBytes;
    }

    @Override
    public byte[] getDocumentAsPdf() {
        if (pdfBytes != null) {
            return pdfBytes;
        }
        byte[] byteArray = null;
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDFont font = PDType1Font.HELVETICA_BOLD;//changed font
            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                contents.beginText();
                contents.setFont(font, 12);
                contents.newLineAtOffset(100, 700);
                contents.showText(text);
                contents.endText();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            doc.save(out);
            doc.close();

            byteArray = out.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    @Override
    public String getDocumentAsTxt() {
        return text.trim();
    }//trimmed; check if other things need trimming

    @Override
    public int getDocumentTextHashCode() {
        return textHashCode;
    }

    @Override
    public URI getKey() {
        return uri;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentImpl document = (DocumentImpl) o;
        return textHashCode == document.textHashCode &&
                uri.equals(document.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textHashCode, uri);
    }


}
