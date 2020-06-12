package edu.yu.cs.com1320.project.stage1.impl;

import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage1.DocumentStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class DocumentStoreImplTest {
	@Test
	public void putAndDeleteDocumentTest1(){
		String text = "This is a document";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		DocumentStoreImpl store = new DocumentStoreImpl();
		URI uri = null;
		try {
			uri = new URI("https//");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		assertEquals(0  ,store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT) );
		assertEquals(text , store.getDocumentAsTxt(uri));
		assertEquals(true , store.deleteDocument(uri));
		assertEquals(null , store.getDocumentAsTxt(uri));
	}
	@Test
	public void putDocumentNullInput() throws URISyntaxException {
		String text = "This is a document";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		DocumentStoreImpl store = new DocumentStoreImpl();
		URI uri = new URI("hi");
		//uri doesnt exist; no doc to delete; return 0
		assertEquals(0 , store.putDocument(null , uri , DocumentStore.DocumentFormat.TXT));
		store.putDocument(input, uri, DocumentStore.DocumentFormat.TXT);
		//uri exists; doc deleted; return deleted.hashcode
		assertEquals(text.hashCode() , store.putDocument(null , uri , DocumentStore.DocumentFormat.TXT));

	}
	@Test
	public void putDocumentAlreadyExists() throws URISyntaxException {
		String text = "This is a document";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		DocumentStoreImpl store = new DocumentStoreImpl();
		URI uri = new URI("hi");
		store.putDocument(input, uri, DocumentStore.DocumentFormat.TXT);

		String text2 = "This is a doc";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());

		assertEquals(text.hashCode() , store.putDocument(input2 , uri , DocumentStore.DocumentFormat.TXT));



	}

	@Test
	public void getPDFDocumentAsTextorPDFTest() throws FileNotFoundException, URISyntaxException {
		String text = "this is a doc";
		URI uri = new URI("https");
		DocumentStoreImpl store = new DocumentStoreImpl();

		//String to pdf

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

		InputStream input = new ByteArrayInputStream(byteArray);

		//
		String text2 = "doc";
		//String to pdf

		byte[] byteArray2 = null;
		try (PDDocument doc = new PDDocument()) {
			PDPage page = new PDPage();
			doc.addPage(page);
			PDFont font = PDType1Font.HELVETICA_BOLD;
			try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
				contents.beginText();
				contents.setFont(font, 12);
				contents.newLineAtOffset(100, 700);
				contents.showText(text2);
				contents.endText();
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			doc.save(out);
			doc.close();
			byteArray2 = out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream input2 = new ByteArrayInputStream(byteArray2);
		//

		assertEquals(0 , store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF));
		assertEquals("this is a doc" , store.getDocumentAsTxt(uri));
		assertArrayEquals(byteArray , store.getDocumentAsPdf(uri));
		assertEquals(text.hashCode() , store.putDocument(input2 , uri , DocumentStore.DocumentFormat.PDF));
		assertEquals(true , store.deleteDocument(uri));
		assertEquals(false , store.deleteDocument(uri));
		assertEquals(null, store.getDocumentAsPdf(uri));

	}

	@Test
	public void getTXTDocumentAsTextorPDF() throws URISyntaxException {
		String text = "hello world";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri = new URI("https");
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.putDocument(input, uri , DocumentStore.DocumentFormat.TXT);

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


		assertEquals("hello world" , store.getDocumentAsTxt(uri));

		PDDocument document = null;
		try {
			document = PDDocument.load(store.getDocumentAsPdf(uri));
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

		assertEquals("hello world" , text2.trim());
	}


}