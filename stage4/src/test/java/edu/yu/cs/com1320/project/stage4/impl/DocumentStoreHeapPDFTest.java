package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.stage4.DocumentStore;
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

public class DocumentStoreHeapPDFTest {
	private URI uri1;
	private String txt1;
	private byte[] pdfData1;
	private String pdfTxt1;
	private InputStream input1;

	private URI uri2;
	private String txt2;
	private byte[] pdfData2;
	private String pdfTxt2;
	private InputStream input2;

	private URI uri3;
	private String txt3;
	private byte[] pdfData3;
	private String pdfTxt3;
	private InputStream input3;

	private URI uri4;
	private String txt4;
	private byte[] pdfData4;
	private String pdfTxt4;
	private InputStream input4;

	private URI uri5;
	private String txt5;
	private byte[] pdfData5;
	private String pdfTxt5;
	private InputStream input5;

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


	@Before
	public void init() throws Exception {
		//init possible values for doc1
		this.uri1 = new URI("uri1");
		this.txt1 = "doc1";
		this.input1 = createPDFinput(txt1);
		//this.pdfTxt1 = "This is some PDF text for doc1, hat tip to Adobe.";
		//this.pdfData1 = textToPdfData(this.pdfTxt1);

		this.uri2 = new URI("uri2");
		this.txt2 = "doc2";
		this.input2 = createPDFinput(txt2);

		this.uri3 = new URI("uri3");
		this.txt3 = "doc3";
		this.input3 = createPDFinput(txt3);

		this.uri4 = new URI("uri4");
		this.txt4 = "doc4";
		this.input4 = createPDFinput(txt4);

		this.uri5 = new URI("uri5");
		this.txt5 = "doc5";
		this.input5= createPDFinput(txt5);

		this.uri6 = new URI("uri6");
		this.txt6 = "this is an undoable 1";
		this.input6 = createPDFinput(txt6);

		this.uri7 = new URI("uri7");
		this.txt7 = "this is an undoable 2";
		this.input7 = createPDFinput(txt7);

	}

	@Test
	public void setAfterTest() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.setMaxDocumentCount(1);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
	}

	@Test
	public void getDocTextReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.getDocumentAsTxt(uri1);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));

	}
	@Test
	public void getDocTextReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));

	}
	@Test
	public void getDocPDFReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.getDocumentAsPdf(uri1);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
	}
	@Test
	public void getDocPDFReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
	}
	@Test
	public void searchReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.search("doc1");
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
	}
	@Test
	public void searchReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
	}
	@Test
	public void searchPdfReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.searchPDFs("doc1");
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
	}
	@Test
	public void searchPdfReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));

	}

	@Test
	public void searchPrefixReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.searchByPrefix("doc1");
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));

	}
	@Test
	public void searchPrefixReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));

	}
	@Test
	public void searchPDFPrefixReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.searchPDFsByPrefix("doc1");
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
	}
	@Test
	public void searchPDFPrefixReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
	}

	@Test
	public void simpleDeleteInHeap() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.deleteDocument(uri2);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));
		assertEquals("doc4" , store.getDocumentAsTxt(uri4));
	}



	@Test
	public void simpleOverMaxDocs() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		//assertEquals("doc1" , store.getDocumentAsTxt(uri1));
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri1));
	}

	@Test
	public void replaceTest() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri1 , DocumentStore.DocumentFormat.PDF);
		//assertEquals("doc1" , store.getDocumentAsTxt(uri1));
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals("doc2" , store.getDocumentAsTxt(uri1));
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null, store.getDocumentAsTxt(uri1));
		assertEquals("doc3", store.getDocumentAsTxt(uri3));
	}

	@Test
	public void putSameDoc() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		String a = "doc1";
		InputStream i = createPDFinput(a);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(i , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);

		assertEquals("doc1", store.getDocumentAsTxt(uri1));
		assertEquals(null, store.getDocumentAsTxt(uri2));
	}

	@Test
	public void test2() throws URISyntaxException {
		String a = "a";
		System.out.println(a.getBytes().length);

	}

	/////////////////////////Lists///////////////////////////////////////////
	@Test
	public void searchList() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		store.search("this");
		assertEquals(store.getDocument(uri6).getLastUseTime() , store.getDocument(uri7).getLastUseTime());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri3));
		assertEquals("this is an undoable 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this is an undoable 2" , store.getDocumentAsTxt(uri7));
	}
	@Test
	public void searchPDFList() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		store.searchPDFs("this");
		assertEquals(store.getDocument(uri6).getLastUseTime() , store.getDocument(uri7).getLastUseTime());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri3));
		assertEquals("this is an undoable 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this is an undoable 2" , store.getDocumentAsTxt(uri7));
	}
	@Test
	public void searchPrefixList() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		store.searchByPrefix("undo");
		assertEquals(store.getDocument(uri6).getLastUseTime() , store.getDocument(uri7).getLastUseTime());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri3));
		assertEquals("this is an undoable 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this is an undoable 2" , store.getDocumentAsTxt(uri7));
	}

	@Test
	public void searchPDFPrefixList() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		store.searchPDFsByPrefix("undo");
		assertEquals(store.getDocument(uri6).getLastUseTime() , store.getDocument(uri7).getLastUseTime());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri3));
		assertEquals("this is an undoable 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this is an undoable 2" , store.getDocumentAsTxt(uri7));
	}



	/////////////////////////UNDO////////////////////////////////////
	@Test
	public void undoPutDocsUsed() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.undo();
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));
	}

	@Test
	public void undoDeleteDocsUsed() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.deleteDocument(uri1);
		store.undo();
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));
	}

	@Test
	public void undoReplaceDocsUsed() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.undo();
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.PDF);
		assertEquals(null , store.getDocumentAsTxt(uri1));
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));
		assertEquals("doc4" , store.getDocumentAsTxt(uri4));
	}

	@Test
	public void undoDeleteAll() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		store.deleteAll("this");
		store.undo();
		store.putDocument(input5 , uri5 , DocumentStore.DocumentFormat.PDF);

		assertEquals(null , store.getDocumentAsTxt(uri3));
		assertEquals("this is an undoable 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this is an undoable 2" , store.getDocumentAsTxt(uri7));

	}

	@Test(expected = IllegalStateException.class)
	public void deleteFromStackCheck1() throws IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		String a = "doc1";
		InputStream i = createPDFinput(a);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(i , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);

		store.undo(uri1);

	}
	@Test(expected = IllegalStateException.class)
	public void deleteFromStackCheck2() throws IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		String a = "doc1";
		InputStream i = createPDFinput(a);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(i , uri1 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);

		store.undo();
		store.undo();
		store.undo();
	}

	@Test(expected = IllegalStateException.class)
	public void deleteFromCommandSet() throws IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.setMaxDocumentCount(2);
		String a = "this 1";
		InputStream i = createPDFinput(a);
		String b = "this 2";
		InputStream i2 = createPDFinput(b);
		store.putDocument(input6, uri6, DocumentStore.DocumentFormat.PDF);
		store.putDocument(input7, uri7, DocumentStore.DocumentFormat.PDF);
		store.deleteAll("this");
		store.putDocument(i, uri6, DocumentStore.DocumentFormat.PDF);
		store.putDocument(i2, uri7, DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3, uri3, DocumentStore.DocumentFormat.PDF);
		store.undo(uri6);
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

	private String pdfToString(byte[] array) throws IOException {
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
}