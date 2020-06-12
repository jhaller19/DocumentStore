package edu.yu.cs.com1320.project.stage4.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class Qucik {

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
			this.pdfData = stringToPDF(this.pdfString);
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
		public void testGetTextDocumentAsPdf() {
			DocumentImpl textDocument = new DocumentImpl(this.textUri, this.textString, this.textHashCode);
			byte[] pdfBytes = textDocument.getDocumentAsPdf();
			String textAsPdfString = pdfDataToText(pdfBytes);
			assertEquals(this.textString, textAsPdfString);
		}

		@Test
		public void testGetPdfDocumentAsPdf() {
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
			} catch (URISyntaxException e) {
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
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			assertNotEquals(this.pdfUri, fakeUri);
		}

	private String pdfDataToText(byte[] a){
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
		return text.trim();
	}

	private byte[] stringToPDF(String text) {
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
}