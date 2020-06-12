package edu.yu.cs.com1320.project.stage3.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class DocumentImplTest {

	@Test
	public void wordCount() throws URISyntaxException {
		URI uri = new URI("uri");
		String text = "Cool cOol coOl cooL";
		DocumentImpl doc = new DocumentImpl(uri , text , text.hashCode());
		assertEquals(4 , doc.wordCount("COOL"));
	}

	@Test
	public void wordCountAlpha() throws URISyntaxException {
		URI uri = new URI("uri");
		String text = "Co&ol cO%ol co&Ol co(oL";
		DocumentImpl doc = new DocumentImpl(uri , text , text.hashCode());
		assertEquals(4 , doc.wordCount("COOL"));

	}

	@Test
	public void getDocumentAsPdf() {
		String text = "text";
		URI uri = null;
		try {
			uri = new URI("hello");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		DocumentImpl doc = new DocumentImpl(uri , text , text.hashCode());
		PDDocument document = null;
		try {
			document = PDDocument.load(doc.getDocumentAsPdf());
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
		String text2 = null;
		for (int p = 1; p <= document.getNumberOfPages(); ++p) {
            /*stripper.setStartPage(p);
            stripper.setEndPage(p);*/
			try {
				text2 = stripper.getText(document);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		assertEquals(text , text2.trim());
	}

	@Test
	public void getDocumentAsTxt() {
		String text = "text";
		URI uri = null;
		try {
			uri = new URI("hello");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		DocumentImpl doc = new DocumentImpl(uri , text , text.hashCode());
		assertEquals(text , doc.getDocumentAsTxt());
	}

	@Test
	public void getDocumentTextHashCode() {
		String text = "text";
		URI uri = null;
		try {
			uri = new URI("hello");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		DocumentImpl doc = new DocumentImpl(uri , text , text.hashCode());
		assertEquals(text.hashCode() , doc.getDocumentTextHashCode());
	}

	@Test
	public void getKey() {
		String text = "text";
		URI uri = null;
		try {
			uri = new URI("hello");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		DocumentImpl doc = new DocumentImpl(uri , text , text.hashCode());
		assertEquals(uri , doc.getKey());
	}

	@Test
	public void getPdfAsPdf(){
		String text = "this is a doc";
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

		URI uri = null;
		try {
			uri = new URI("Pizza");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		DocumentImpl document = new DocumentImpl(uri , text , text.hashCode() , byteArray);
		assertEquals(text , document.getDocumentAsTxt());
		assertEquals(text.hashCode() , document.getDocumentTextHashCode());
		assertEquals(byteArray , document.getDocumentAsPdf());
		assertEquals(uri , document.getKey());


	}
}