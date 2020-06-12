package edu.yu.cs.com1320.project.stage5.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class DocumentImplTest {
    private URI textUri;
    private String textString;
    private int textHashCode;

    private URI pdfUri;
    private String pdfString;
    private int pdfHashCode;
    private byte[] pdfData;

    @Before
    public void setUp() throws Exception {
        this.textUri = new URI("http://edu.yu.cs/com1320/txt");
        this.textString = "This is text content. Lots of it.";
        this.textHashCode = this.textString.hashCode();

        this.pdfUri = new URI("http://edu.yu.cs/com1320/pdf");
        this.pdfString = "This is a PDF, brought to you by Adobe.";
        this.pdfHashCode = this.pdfString.hashCode();
        this.pdfData = textToPdfData(this.pdfString);
    }

    private byte[] textToPdfData(String text){
        byte[] byteArray = null;
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDFont font = PDType1Font.HELVETICA_BOLD;
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

    private String pdfDataToText (byte[] array) throws IOException {
        InputStream input = new ByteArrayInputStream(array);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];//NOT SURE
        int length = 0;
        while(true) {
            try {
                if (!((length = input.read(buf)) != -1)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            baos.write(buf , 0 , length);
        }
        byte [] a = baos.toByteArray();

        PDDocument document = null;
        try {
            document = PDDocument.load(a);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PDFTextStripper stripper = null;
        try {
            stripper = new PDFTextStripper();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stripper.setSortByPosition(true);
        String text = null;
        for (int p = 1; p <= document.getNumberOfPages(); ++p) {
            /*stripper.setStartPage(p);
            stripper.setEndPage(p);*/
            try {
                text = stripper.getText(document);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        document.close();
        return text.trim();
    }

    @Test
    public void testStage3WordCount() {
        DocumentImpl textDocument = new DocumentImpl(this.textUri, this.textString, this.textHashCode);
        assertEquals(1, textDocument.wordCount("This"));
        assertEquals(0, textDocument.wordCount("blah"));
    }

    @Test
    public void testStage3CaseInsensitive() {
        DocumentImpl textDocument = new DocumentImpl(this.textUri, this.textString, this.textHashCode);
        assertEquals(1, textDocument.wordCount("this"));
        assertEquals(1, textDocument.wordCount("tHis"));
    }

    @Test
    public void testGetTextDocumentAsTxt() {
        DocumentImpl textDocument = new DocumentImpl(this.textUri, this.textString, this.textHashCode);
        assertEquals(this.textString, textDocument.getDocumentAsTxt());
    }

    @Test
    public void testGetPdfDocumentAsTxt() {
        DocumentImpl pdfDocument = new DocumentImpl(this.pdfUri, this.pdfString, this.pdfHashCode, this.pdfData);
        assertEquals(this.pdfString, pdfDocument.getDocumentAsTxt());
    }

    @Test
    public void testGetTextDocumentAsPdf() throws IOException {
        DocumentImpl textDocument = new DocumentImpl(this.textUri, this.textString, this.textHashCode);
        byte[] pdfBytes = textDocument.getDocumentAsPdf();
        String textAsPdfString = pdfDataToText(pdfBytes);
        assertEquals(this.textString, textAsPdfString);
    }

    @Test
    public void testGetPdfDocumentAsPdf() throws IOException {
        DocumentImpl pdfDocument = new DocumentImpl(this.pdfUri, this.pdfString, this.pdfHashCode, this.pdfData);
        byte[] pdfBytes = pdfDocument.getDocumentAsPdf();
        String pdfAsPdfString = pdfDataToText(pdfBytes);
        assertEquals(this.pdfString, pdfAsPdfString);
    }

    @Test
    public void testGetTextDocumentTextHashCode() {
        DocumentImpl textDocument = new DocumentImpl(this.textUri, this.textString, this.textHashCode);
        assertEquals(this.textHashCode, textDocument.getDocumentTextHashCode());
    }

    @Test
    public void testGetPdfDocumentTextHashCode() {
        DocumentImpl pdfDocument = new DocumentImpl(this.pdfUri, this.pdfString, this.pdfHashCode, this.pdfData);
        assertEquals(this.pdfHashCode, pdfDocument.getDocumentTextHashCode());
        assertNotEquals(this.pdfHashCode, 25);
    }

    @Test
    public void testGetTextDocumentKey() {
        DocumentImpl textDocument = new DocumentImpl(this.textUri, this.textString, this.textHashCode);
        assertEquals(this.textUri, textDocument.getKey());
        URI fakeUri = null;
        try {
            fakeUri = new URI("http://wrong.com");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
        assertNotEquals(this.textUri, fakeUri);
    }

    @Test
    public void testGetPdfDocumentKey() {
        DocumentImpl pdfDocument = new DocumentImpl(this.pdfUri, this.pdfString, this.pdfHashCode, this.pdfData);
        assertEquals(this.pdfUri, pdfDocument.getKey());
        URI fakeUri = null;
        try {
            fakeUri = new URI("http://wrong.com");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
        assertNotEquals(this.pdfUri, fakeUri);
    }

    @Test
    public void stage4TestSetGetLastUseTime(){
        long start = System.nanoTime();
        DocumentImpl doc = new DocumentImpl(this.pdfUri, this.pdfString, this.pdfHashCode, this.pdfData);
        doc.setLastUseTime(System.nanoTime());
        assertTrue("last use time should've been > " + start,start < doc.getLastUseTime());
    }
}