package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
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
	public void judahsTest() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "1";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "2";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		String text3 = "3";
		InputStream input3 = new ByteArrayInputStream(text3.getBytes());
		URI uri3 =  new URI("3");
		//
		String text4 = "4";
		InputStream input4 = new ByteArrayInputStream(text4.getBytes());
		URI uri4 =  new URI("4");
		//
		String text5 = "5";
		InputStream input5 = new ByteArrayInputStream(text5.getBytes());
		URI uri5 =  new URI("5");
		//
		String text6 = "6";
		InputStream input6 = new ByteArrayInputStream(text6.getBytes());
		URI uri6 =  new URI("6");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		//assertEquals(1 , store.commandStack.size());
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		//assertEquals(2 , store.commandStack.size());
		store.putDocument(input3 , uri , DocumentStore.DocumentFormat.TXT);
		//assertEquals(3 , store.commandStack.size());
		assertEquals("3" , store.getDocumentAsTxt(uri));
		store.undo(uri);
		//assertEquals(2 , store.commandStack.size());
		assertEquals("1" , store.getDocumentAsTxt(uri));
		store.undo();
		//assertEquals(1 , store.commandStack.size());
		assertEquals(null, store.getDocumentAsTxt(uri2));
		store.putDocument(input4 , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals("4", store.getDocumentAsTxt(uri));
		//assertEquals(2 , store.commandStack.size());
		store.putDocument(input5 , uri3 , DocumentStore.DocumentFormat.TXT);
		//assertEquals(3 , store.commandStack.size());
		store.putDocument(input6 , uri , DocumentStore.DocumentFormat.TXT);
		//assertEquals(4 , store.commandStack.size());
		assertEquals("6", store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals("4", store.getDocumentAsTxt(uri));
		//assertEquals(3 , store.commandStack.size());
		store.undo(uri);
		//assertEquals(2 , store.commandStack.size());
		assertEquals("1", store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals(null , store.getDocumentAsTxt(uri3));
		//assertEquals(1 , store.commandStack.size());


	}
	@Test
	public void judahsTestPDF() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "1";
		InputStream input = createPDFinput(text);
		URI uri =  new URI("1");
		//
		String text2 = "2";
		InputStream input2 = createPDFinput(text2);
		URI uri2 =  new URI("2");
		//
		String text3 = "3";
		InputStream input3 = createPDFinput(text3);
		URI uri3 =  new URI("3");
		//
		String text4 = "4";
		InputStream input4 = createPDFinput(text4);
		URI uri4 =  new URI("4");
		//
		String text5 = "5";
		InputStream input5 = createPDFinput(text5);
		URI uri5 =  new URI("5");
		//
		String text6 = "6";
		InputStream input6 = createPDFinput(text6);
		URI uri6 =  new URI("6");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		//assertEquals(1 , store.commandStack.size());
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		//assertEquals(2 , store.commandStack.size());
		store.putDocument(input3 , uri , DocumentStore.DocumentFormat.PDF);
		//assertEquals(3 , store.commandStack.size());
		assertEquals("3" , store.getDocumentAsTxt(uri));
		store.undo(uri);
		//assertEquals(2 , store.commandStack.size());
		assertEquals("1" , store.getDocumentAsTxt(uri));
		store.undo();
		//assertEquals(1 , store.commandStack.size());
		assertEquals(null, store.getDocumentAsTxt(uri2));
		store.putDocument(input4 , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals("4", store.getDocumentAsTxt(uri));
		//assertEquals(2 , store.commandStack.size());
		store.putDocument(input5 , uri3 , DocumentStore.DocumentFormat.PDF);
		//assertEquals(3 , store.commandStack.size());
		store.putDocument(input6 , uri , DocumentStore.DocumentFormat.PDF);
		//assertEquals(4 , store.commandStack.size());
		assertEquals("6", store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals("4", store.getDocumentAsTxt(uri));
		//assertEquals(3 , store.commandStack.size());
		store.undo(uri);
		//assertEquals(2 , store.commandStack.size());
		assertEquals("1", store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals(null , store.getDocumentAsTxt(uri3));
		//assertEquals(1 , store.commandStack.size());

	}

	@Test
	public void commandDoesNothingPutNull() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "1";
		InputStream input  = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		store.putDocument(null, uri , DocumentStore.DocumentFormat.TXT);
		store.undo();
	}
	@Test
	public void commandDoesNothingDelete() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "1";
		InputStream input  = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		assertEquals(false, store.deleteDocument(uri));
		store.undo();
	}
	@Test
	public void commandDoesNothingPutDocExists() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "1";
		String text2 = "1";
		InputStream input  = new ByteArrayInputStream(text.getBytes());
		InputStream input2  = new ByteArrayInputStream(text2.getBytes());
		URI uri =  new URI("1");
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri , DocumentStore.DocumentFormat.TXT);
		store.undo();
		store.undo();
	}

	@Test
	public void complexTest2() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "1";
		InputStream input = createPDFinput(text);
		URI uri =  new URI("1");
		//
		String text2 = "2";
		InputStream input2 = createPDFinput(text2);
		URI uri2 =  new URI("2");
		//
		String text3 = "3";
		InputStream input3 = createPDFinput(text3);
		URI uri3 =  new URI("3");
		//
		String text4 = "4";
		InputStream input4 = createPDFinput(text4);
		URI uri4 =  new URI("4");
		//
		String text5 = "5";
		InputStream input5 = createPDFinput(text5);
		URI uri5 =  new URI("5");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		store.deleteDocument(uri);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3, DocumentStore.DocumentFormat.PDF);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input5 , uri5 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri));
		store.undo(uri);
		assertEquals("1" , store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals(null , store.getDocumentAsTxt(uri5));
		store.undo(uri2);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		store.undo();
		store.undo();
		assertEquals(null , store.getDocumentAsTxt(uri4));
		assertEquals(null , store.getDocumentAsTxt(uri3));
		assertEquals("1" , store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals(null , store.getDocumentAsTxt(uri));


	}

	@Test
	public void complexTest1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "1";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "2";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		String text3 = "3";
		InputStream input3 = new ByteArrayInputStream(text3.getBytes());
		URI uri3 =  new URI("3");
		//
		String text4 = "4";
		InputStream input4 = new ByteArrayInputStream(text4.getBytes());
		URI uri4 =  new URI("4");
		//
		String text5 = "5";
		InputStream input5 = new ByteArrayInputStream(text5.getBytes());
		URI uri5 =  new URI("5");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.deleteDocument(uri);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input5 , uri5 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri));
		store.undo(uri);
		assertEquals("1" , store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals(null , store.getDocumentAsTxt(uri5));
		store.undo(uri2);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		store.undo();
		store.undo();
		assertEquals(null , store.getDocumentAsTxt(uri4));
		assertEquals(null , store.getDocumentAsTxt(uri3));
		assertEquals("1" , store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals(null , store.getDocumentAsTxt(uri));

	}


	@Test
	public void multipleURIReplaceUndo1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "1";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "2";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		String text3 = "3";
		InputStream input3 = new ByteArrayInputStream(text3.getBytes());
		URI uri3 =  new URI("3");
		//
		String text4 = "4";
		InputStream input4 = new ByteArrayInputStream(text4.getBytes());
		URI uri4 =  new URI("4");
		//
		String text5 = "5";
		InputStream input5 = new ByteArrayInputStream(text5.getBytes());
		URI uri5 =  new URI("1");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input5 , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);
		assertEquals("5" , store.getDocumentAsTxt(uri));
		store.undo(uri5);
		assertEquals("1" , store.getDocumentAsTxt(uri));

	}
	@Test
	public void multipleURIUndo1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "1";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "2";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		String text3 = "3";
		InputStream input3 = new ByteArrayInputStream(text3.getBytes());
		URI uri3 =  new URI("3");
		//
		String text4 = "4";
		InputStream input4 = new ByteArrayInputStream(text4.getBytes());
		URI uri4 =  new URI("4");
		//
		String text5 = "5";
		InputStream input5 = new ByteArrayInputStream(text5.getBytes());
		URI uri5 =  new URI("5");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input5 , uri5 , DocumentStore.DocumentFormat.TXT);
		assertEquals("1" , store.getDocumentAsTxt(uri));
		assertEquals("2" , store.getDocumentAsTxt(uri2));
		assertEquals("3" , store.getDocumentAsTxt(uri3));
		assertEquals("4" , store.getDocumentAsTxt(uri4));
		assertEquals("5" , store.getDocumentAsTxt(uri5));
		store.undo(uri);
		store.undo();
		store.undo();
		store.undo();
		store.undo();
		assertEquals(null , store.getDocument(uri));
		assertEquals(null , store.getDocument(uri4));
		assertEquals(null , store.getDocument(uri3));
		assertEquals(null , store.getDocument(uri2));
		assertEquals(null , store.getDocument(uri));

	}

	@Test
	public void multipleSimpleUndo1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "1";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "2";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		String text3 = "3";
		InputStream input3 = new ByteArrayInputStream(text3.getBytes());
		URI uri3 =  new URI("3");
		//
		String text4 = "4";
		InputStream input4 = new ByteArrayInputStream(text4.getBytes());
		URI uri4 =  new URI("4");
		//
		String text5 = "5";
		InputStream input5 = new ByteArrayInputStream(text5.getBytes());
		URI uri5 =  new URI("5");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input5 , uri5 , DocumentStore.DocumentFormat.TXT);
		assertEquals("1" , store.getDocumentAsTxt(uri));
		assertEquals("2" , store.getDocumentAsTxt(uri2));
		assertEquals("3" , store.getDocumentAsTxt(uri3));
		assertEquals("4" , store.getDocumentAsTxt(uri4));
		assertEquals("5" , store.getDocumentAsTxt(uri5));
		store.undo();
		store.undo();
		store.undo();
		store.undo();
		store.undo();
		assertEquals(null , store.getDocument(uri5));
		assertEquals(null , store.getDocument(uri4));
		assertEquals(null , store.getDocument(uri3));
		assertEquals(null , store.getDocument(uri2));
		assertEquals(null , store.getDocument(uri));

	}

	@Test
	public void simpleUndoPut1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("hi");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals("hello" , store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals(null , store.getDocument(uri));
	}
	@Test
	public void simpleUndoMultipleActions1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("hi");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals("hello" , store.getDocumentAsTxt(uri));
		store.deleteDocument(uri);
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.undo();
		store.undo();
		assertEquals("hello" , store.getDocumentAsTxt(uri));
	}
	@Test
	public void URIUndoPut1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("hi");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals("hello" , store.getDocumentAsTxt(uri));
		store.undo(uri);
		assertEquals(null , store.getDocument(uri));
	}
	@Test
	public void simpleUndoDelete1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("hi");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals("hello" , store.getDocumentAsTxt(uri));
		store.deleteDocument(uri);
		assertEquals(null , store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
	}
	@Test
	public void URIUndoDelete1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("hi");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals("hello" , store.getDocumentAsTxt(uri));
		store.deleteDocument(uri);
		assertEquals(null , store.getDocumentAsTxt(uri));
		store.undo(uri);
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
	}
	@Test
	public void simpleUndoPutNull1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("hi");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals("hello" , store.getDocumentAsTxt(uri));
		store.putDocument(null , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
	}
	@Test
	public void URIUndoPutNull1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("hi");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals("hello" , store.getDocumentAsTxt(uri));
		store.putDocument(null , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri));
		store.undo(uri);
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
	}
	@Test
	public void simpleUndoReplaceSameURI1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("hi");
		//
		String text2 = "hi";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals("hi" , store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals("hello" , store.getDocumentAsTxt(uri));
	}
	@Test
	public void URIUndoReplaceSameURI1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("hi");
		//
		String text2 = "hi";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals("hi" , store.getDocumentAsTxt(uri));
		store.undo(uri);
		assertEquals("hello" , store.getDocumentAsTxt(uri));
	}

	////////////////////////////////////PDF////////////////////////////////////////////////////////
	@Test
	public void multipleSimplePDFUndo1() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "1";
		InputStream input = createPDFinput(text);
		URI uri =  new URI("1");
		//
		String text2 = "2";
		InputStream input2 = createPDFinput(text2);
		URI uri2 =  new URI("2");
		//
		String text3 = "3";
		InputStream input3 = createPDFinput(text3);
		URI uri3 =  new URI("3");
		//
		String text4 = "4";
		InputStream input4 = createPDFinput(text4);
		URI uri4 =  new URI("4");
		//
		String text5 = "5";
		InputStream input5 = createPDFinput(text5);
		URI uri5 =  new URI("5");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3, DocumentStore.DocumentFormat.PDF);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input5 , uri5 , DocumentStore.DocumentFormat.PDF);
		assertEquals("1" , store.getDocumentAsTxt(uri));
		assertEquals("2" , store.getDocumentAsTxt(uri2));
		assertEquals("3" , store.getDocumentAsTxt(uri3));
		assertEquals("4" , store.getDocumentAsTxt(uri4));
		assertEquals("5" , store.getDocumentAsTxt(uri5));
		store.undo();
		store.undo();
		store.undo();
		store.undo();
		store.undo();
		assertEquals(null , store.getDocument(uri));
		assertEquals(null , store.getDocument(uri4));
		assertEquals(null , store.getDocument(uri3));
		assertEquals(null , store.getDocument(uri2));
		assertEquals(null , store.getDocument(uri));

	}

	@Test
	public void multipleURIPDFUndo1() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "1";
		InputStream input = createPDFinput(text);
		URI uri =  new URI("1");
		//
		String text2 = "2";
		InputStream input2 = createPDFinput(text2);
		URI uri2 =  new URI("2");
		//
		String text3 = "3";
		InputStream input3 = createPDFinput(text3);
		URI uri3 =  new URI("3");
		//
		String text4 = "4";
		InputStream input4 = createPDFinput(text4);
		URI uri4 =  new URI("4");
		//
		String text5 = "5";
		InputStream input5 = createPDFinput(text5);
		URI uri5 =  new URI("5");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3, DocumentStore.DocumentFormat.PDF);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input5 , uri5 , DocumentStore.DocumentFormat.PDF);
		assertEquals("1" , store.getDocumentAsTxt(uri));
		assertEquals("2" , store.getDocumentAsTxt(uri2));
		assertEquals("3" , store.getDocumentAsTxt(uri3));
		assertEquals("4" , store.getDocumentAsTxt(uri4));
		assertEquals("5" , store.getDocumentAsTxt(uri5));
		store.undo(uri);
		store.undo();
		store.undo();
		store.undo();
		store.undo();
		assertEquals(null , store.getDocument(uri));
		assertEquals(null , store.getDocument(uri4));
		assertEquals(null , store.getDocument(uri3));
		assertEquals(null , store.getDocument(uri2));
		assertEquals(null , store.getDocument(uri));

	}

	@Test
	public void simpleUndoPutPDF1() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = createPDFinput(text);
		URI uri = new URI("hi");

		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals("hello" , store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals(null , store.getDocument(uri));
	}
	@Test
	public void URIUndoPutPDF1() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = createPDFinput(text);
		URI uri = new URI("hi");

		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals("hello" , store.getDocumentAsTxt(uri));
		store.undo(uri);
		assertEquals(null , store.getDocument(uri));
	}
	@Test
	public void simpleUndoPutNullPDF1() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = createPDFinput(text);
		URI uri = new URI("hi");

		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
		store.putDocument(null , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
	}
	@Test
	public void URIUndoPutNullPDF1() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = createPDFinput(text);
		URI uri = new URI("hi");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
		store.putDocument(null , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri));
		store.undo(uri);
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
	}
	@Test
	public void simpleUndoDeletePDF1() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = createPDFinput(text);
		URI uri = new URI("hi");

		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
		store.deleteDocument(uri);
		assertEquals(null , store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
	}
	@Test
	public void URIUndoDeletePDF1() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = createPDFinput(text);
		URI uri = new URI("hi");

		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
		store.deleteDocument(uri);
		assertEquals(null , store.getDocumentAsTxt(uri));
		store.undo(uri);
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
	}
	@Test
	public void simpleUndoReplacePDF1() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		//
		String text = "hello";
		InputStream input = createPDFinput(text);
		URI uri = new URI("hi");
		//
		String text2 = "hi";
		InputStream input2 = createPDFinput(text2);
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
		store.putDocument(input2 , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals("hi" , store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
	}
	@Test
	public void URIUndoReplacePDF1() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		//
		String text = "hello";
		InputStream input = createPDFinput(text);
		URI uri = new URI("hi");
		//
		String text2 = "hi";
		InputStream input2 = createPDFinput(text2);

		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
		store.putDocument(input2 , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals("hi" , store.getDocumentAsTxt(uri));
		store.undo(uri);
		assertEquals("hello" , store.getDocument(uri).getDocumentAsTxt());
	}

	private InputStream createPDFinput(String text) throws IOException {
		byte[] byteArray = null;
		PDDocument doc = new PDDocument();
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
		InputStream input = new ByteArrayInputStream(byteArray);
		return input;
	}
	///////////////////////////////////Exceptions//////////////////////////////////////////////////


	@Test(expected = IllegalStateException.class)
	public void URINotFoundUndo() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "hello";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("hi");
		URI uri2  = new URI("not");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.undo(uri2);
	}

	@Test(expected = IllegalStateException.class)
	public void commandStackEmptySimpleUndo() {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.undo();
	}
	@Test(expected = IllegalStateException.class)
	public void commandStackEmptyURIUndo() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		URI uri = new URI("hi");
		store.undo(uri);
	}








	///////////////////////////////////////////////////////////////////////////////////////



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
