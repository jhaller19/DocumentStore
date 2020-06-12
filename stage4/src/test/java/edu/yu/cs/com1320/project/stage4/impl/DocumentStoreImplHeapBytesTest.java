package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.stage4.DocumentStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class DocumentStoreImplHeapBytesTest {
	private URI uri1;
	private String txt1;
	private byte[] pdfData1;
	private String pdfTxt1;
	private InputStream input1;
	private InputStream pInput1;

	private URI uri2;
	private String txt2;
	private byte[] pdfData2;
	private String pdfTxt2;
	private InputStream input2;
	private InputStream pInput2;


	private URI uri3;
	private String txt3;
	private byte[] pdfData3;
	private String pdfTxt3;
	private InputStream input3;
	private InputStream pInput3;


	private URI uri4;
	private String txt4;
	private byte[] pdfData4;
	private String pdfTxt4;
	private InputStream input4;
	private InputStream pInput4;


	private URI uri5;
	private String txt5;
	private byte[] pdfData5;
	private String pdfTxt5;
	private InputStream input5;
	private InputStream pInput5;

	private URI uri6;
	private String txt6;
	private byte[] pdfData6;
	private String pdfTxt6;
	private InputStream input6;

	private URI uri7;
	private String txt7;
	private byte[] pdfData7;
	private String pdfTxt7;
	private InputStream input7;

	private URI uri8;
	private String txt8;
	private byte[] pdfData8;
	private String pdfTxt8;
	private InputStream input8;



	@Before
	public void init() throws Exception {
		//init possible values for doc1
		this.uri1 = new URI("uri1");
		this.txt1 = "doc1";
		this.input1 = new ByteArrayInputStream(txt1.getBytes());
		this.pInput1 = createPDFinput(txt1);
		//this.pdfTxt1 = "This is some PDF text for doc1, hat tip to Adobe.";
		//this.pdfData1 = textToPdfData(this.pdfTxt1);

		this.uri2 = new URI("uri2");
		this.txt2 = "doc2";
		this.input2 = new ByteArrayInputStream(txt2.getBytes());
		this.pInput2 = createPDFinput(txt2);


		this.uri3 = new URI("uri3");
		this.txt3 = "doc3";
		this.input3 = new ByteArrayInputStream(txt3.getBytes());
		this.pInput3 = createPDFinput(txt3);


		this.uri4 = new URI("uri4");
		this.txt4 = "doc4";
		this.input4 = new ByteArrayInputStream(txt4.getBytes());
		this.pInput4 = createPDFinput(txt4);


		this.uri5 = new URI("uri5");
		this.txt5 = "doc5";
		this.input5 = new ByteArrayInputStream(txt5.getBytes());
		this.pInput5 = createPDFinput(txt5);

		this.uri6 = new URI("uri6");
		this.txt6 = "this 1";
		this.input6 = new ByteArrayInputStream(txt6.getBytes());

		this.uri7 = new URI("uri7");
		this.txt7 = "this 2";
		this.input7 = new ByteArrayInputStream(txt7.getBytes());
	}

	@Test
	public void setAfterTest() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.setMaxDocumentBytes(832);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
	}

	@Test
	public void getDocTextReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.getDocumentAsTxt(uri1);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));

	}
	@Test
	public void getDocTextReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));

	}
	@Test
	public void getDocPDFReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.getDocumentAsPdf(uri1);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
	}
	@Test
	public void getDocPDFReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
	}
	@Test
	public void searchReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.search("doc1");
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
	}
	@Test
	public void searchReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
	}
	@Test
	public void searchPdfReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.searchPDFs("doc1");
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
	}
	@Test
	public void searchPdfReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));

	}

	@Test
	public void searchPrefixReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.searchByPrefix("doc1");
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));

	}
	@Test
	public void searchPrefixReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));

	}
	@Test
	public void searchPDFPrefixReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.searchPDFsByPrefix("doc1");
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
	}
	@Test
	public void searchPDFPrefixReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
	}

	@Test
	public void simpleDeleteInHeap() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.deleteDocument(uri2);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));
		assertEquals("doc4" , store.getDocumentAsTxt(uri4));
	}

	@Test
	public void simpleOverMaxDocs() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		//assertEquals("doc1" , store.getDocumentAsTxt(uri1));
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		//System.out.println(store.getDocumentAsPdf(uri2).length + store.getDocumentAsTxt(uri2).length());
		assertEquals(null , store.getDocumentAsTxt(uri1));
	}

	@Test
	public void replaceTest() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri1 , DocumentStore.DocumentFormat.TXT);
		//assertEquals("doc1" , store.getDocumentAsTxt(uri1));
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals("doc2" , store.getDocumentAsTxt(uri1));
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null, store.getDocumentAsTxt(uri1));
		assertEquals("doc3", store.getDocumentAsTxt(uri3));
	}

	/*@Test
	public void mayerTest() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.TXT);


		//assertEquals("doc1" , store.getDocumentAsTxt(uri1));
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals("doc2" , store.getDocumentAsTxt(uri1));
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null, store.getDocumentAsTxt(uri1));
		assertEquals("doc3", store.getDocumentAsTxt(uri3));
	}*/

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

	///////////////////////////////LISTS//////////////////////////////
	@Test
	public void searchList() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(836*3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.search("this");
		assertEquals(store.getDocument(uri6).getLastUseTime() , store.getDocument(uri7).getLastUseTime());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri3));
		assertEquals("this 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this 2" , store.getDocumentAsTxt(uri7));
	}
	@Test
	public void searchPDFList() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(836*3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.searchPDFs("this");
		assertEquals(store.getDocument(uri6).getLastUseTime() , store.getDocument(uri7).getLastUseTime());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri3));
		assertEquals("this 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this 2" , store.getDocumentAsTxt(uri7));
	}
	@Test
	public void searchPrefixList() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(836*3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.searchByPrefix("this");
		assertEquals(store.getDocument(uri6).getLastUseTime() , store.getDocument(uri7).getLastUseTime());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri3));
		assertEquals("this 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this 2" , store.getDocumentAsTxt(uri7));
	}

	@Test
	public void searchPDFPrefixList() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(836*3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.searchPDFsByPrefix("this");
		assertEquals(store.getDocument(uri6).getLastUseTime() , store.getDocument(uri7).getLastUseTime());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri3));
		assertEquals("this 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this 2" , store.getDocumentAsTxt(uri7));
	}
	@Test
	public void putSameDoc() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		String a = "doc1";
		InputStream i = new ByteArrayInputStream(a.getBytes());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(i , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);

		assertEquals("doc1", store.getDocumentAsTxt(uri1));
		assertEquals(null, store.getDocumentAsTxt(uri2));
	}

	///////////////////////////////////////UNDO//////////////////////////////////////////////
	@Test
	public void undoPut() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.undo();
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));
	}

	@Test
	public void undoDelete() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.deleteDocument(uri1);
		store.undo();
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));
	}

	@Test
	public void undoReplace() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.undo();
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));
		assertEquals("doc4" , store.getDocumentAsTxt(uri4));

	}

	@Test
	public void undoDeleteAll() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(836*3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.deleteAll("this");
		store.undo();
		store.putDocument(input5 , uri5 , DocumentStore.DocumentFormat.TXT);

		assertEquals(null , store.getDocumentAsTxt(uri3));
		assertEquals("this 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this 2" , store.getDocumentAsTxt(uri7));

	}

	@Test(expected = IllegalStateException.class)
	public void deleteFromStackCheck() {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		String a = "doc1";
		InputStream i = new ByteArrayInputStream(a.getBytes());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(i , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);

		store.undo(uri1);

	}

	@Test(expected = IllegalStateException.class)
	public void deleteFromStackCheck2() {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(832*2);
		String a = "doc1";
		InputStream i = new ByteArrayInputStream(a.getBytes());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(i , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);

		store.undo();
		store.undo();
		store.undo();
	}

	@Test(expected = IllegalStateException.class)
	public void deleteFromCommandSet() {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentBytes(836*2);
		String a = "this 1";
		InputStream i = new ByteArrayInputStream(a.getBytes());
		String a2 = "this 2";
		InputStream i2 = new ByteArrayInputStream(a2.getBytes());

		store.putDocument(input6, uri6, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input7, uri7, DocumentStore.DocumentFormat.TXT);
		store.deleteAll("this");
		store.putDocument(i, uri6, DocumentStore.DocumentFormat.TXT);
		store.putDocument(i2, uri7, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3, uri3, DocumentStore.DocumentFormat.TXT);
		store.undo(uri6);
	}
}