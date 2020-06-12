package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.DocumentStore;
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
import java.util.List;

import static org.junit.Assert.*;

public class Stage3Test {
	//variables to hold possible values for doc1
	private URI uri1;
	private String txt1;
	private byte[] pdfData1;
	private String pdfTxt1;

	//variables to hold possible values for doc2
	private URI uri2;
	private String txt2;
	private byte[] pdfData2;
	private String pdfTxt2;

	//variables to hold possible values for doc3
	private URI uri3;
	private String txt3;
	private byte[] pdfData3;
	private String pdfTxt3;

	//variables to hold possible values for doc4
	private URI uri4;
	private String txt4;
	private byte[] pdfData4;
	private String pdfTxt4;
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
	@Before
	public void init() throws Exception {
		//init possible values for doc1
		this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
		this.txt1 = "This is the text of doc1, in plain text. No fancy file format - just plain old String. Computer. Headphones.";
		this.pdfTxt1 = "This is some PDF text for doc1, hat tip to Adobe.";
		this.pdfData1 = textToPdfData(this.pdfTxt1);

		//init possible values for doc2
		this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
		this.txt2 = "Text for doc2. A plain old String.";
		this.pdfTxt2 = "PDF content for doc2: PDF format was opened in 2008.";
		this.pdfData2 = textToPdfData(this.pdfTxt2);

		//init possible values for doc3
		this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
		this.txt3 = "This is the text of doc3";
		this.pdfTxt3 = "This is some PDF text for doc3, hat tip to Adobe.";
		this.pdfData3 = textToPdfData(this.pdfTxt3);

		//init possible values for doc4
		this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
		this.txt4 = "This is the text of doc4";
		this.pdfTxt4 = "This is some PDF text for doc4, which is an open source standard.";
		this.pdfData4 = textToPdfData(this.pdfTxt4);
	}

	@Test
	public void testPutPdfDocumentNoPreviousDocAtURI(){
		DocumentStore store = new DocumentStoreImpl();
		int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
		//TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
		assertTrue(returned == 0 || returned == this.pdfTxt1.hashCode());
	}

	@Test
	public void testPutTxtDocumentNoPreviousDocAtURI(){
		DocumentStore store = new DocumentStoreImpl();
		int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
		//TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
		assertTrue(returned == 0 || returned == this.txt1.hashCode());
	}

	@Test
	public void testPutDocumentWithNullArguments(){
		DocumentStore store = new DocumentStoreImpl();
		try {
			store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), null, DocumentStore.DocumentFormat.TXT);
			fail("null URI should've thrown IllegalArgumentException");
		}catch(IllegalArgumentException e){}
		try {
			store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, null);
			fail("null format should've thrown IllegalArgumentException");
		}catch(IllegalArgumentException e){}
	}

	@Test
	public void testPutNewVersionOfDocumentPdf() throws IOException {
		//put the first version
		DocumentStore store = new DocumentStoreImpl();
		int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
		//TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
		assertTrue(returned == 0 || returned == this.pdfTxt1.hashCode());
		assertEquals("failed to return correct pdf text",this.pdfTxt1,pdfDataToText(store.getDocumentAsPdf(this.uri1)));

		//put the second version, testing both return value of put and see if it gets the correct text
		returned = store.putDocument(new ByteArrayInputStream(this.pdfData2),this.uri1, DocumentStore.DocumentFormat.PDF);
		//TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
		assertTrue("should return hashcode of old text",this.pdfTxt1.hashCode() == returned || this.pdfTxt2.hashCode() == returned);
		assertEquals("failed to return correct pdf text", this.pdfTxt2,pdfDataToText(store.getDocumentAsPdf(this.uri1)));
	}

	@Test
	public void testPutNewVersionOfDocumentTxt(){
		//put the first version
		DocumentStore store = new DocumentStoreImpl();
		int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
		//TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
		assertTrue(returned == 0 || returned == this.txt1.hashCode());
		assertEquals("failed to return correct text",this.txt1,store.getDocumentAsTxt(this.uri1));

		//put the second version, testing both return value of put and see if it gets the correct text
		returned = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
		//TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
		assertTrue("should return hashcode of old text",this.txt1.hashCode() == returned || this.txt2.hashCode() == returned);
		assertEquals("failed to return correct text",this.txt2,store.getDocumentAsTxt(this.uri1));
	}

	@Test
	public void testGetTxtDocAsPdf() throws IOException {
		DocumentStore store = new DocumentStoreImpl();
		int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
		//TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
		assertTrue(returned == 0 || returned == this.txt1.hashCode());
		assertEquals("failed to return correct pdf text",this.txt1,pdfDataToText(store.getDocumentAsPdf(this.uri1)));
	}

	@Test
	public void testGetTxtDocAsTxt(){
		DocumentStore store = new DocumentStoreImpl();
		int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
		//TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
		assertTrue(returned == 0 || returned == this.txt1.hashCode());
		assertEquals("failed to return correct text",this.txt1,store.getDocumentAsTxt(this.uri1));
	}

	@Test
	public void testGetPdfDocAsPdf() throws IOException {
		DocumentStore store = new DocumentStoreImpl();
		int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
		//TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
		assertTrue(returned == 0 || returned == this.pdfTxt1.hashCode());
		assertEquals("failed to return correct pdf text",this.pdfTxt1,pdfDataToText(store.getDocumentAsPdf(this.uri1)));
	}

	@Test
	public void testGetPdfDocAsTxt(){
		DocumentStore store = new DocumentStoreImpl();
		int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
		//TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
		assertTrue(returned == 0 || returned == this.pdfTxt1.hashCode());
		assertEquals("failed to return correct text",this.pdfTxt1,store.getDocumentAsTxt(this.uri1));
	}

	@Test
	public void testDeleteDoc(){
		DocumentStore store = new DocumentStoreImpl();
		store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
		store.deleteDocument(this.uri1);
		assertEquals("calling get on URI from which doc was deleted should've returned null", null, store.getDocumentAsPdf(this.uri1));
	}

	@Test
	public void testDeleteDocReturnValue(){
		DocumentStore store = new DocumentStoreImpl();
		store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
		//should return true when deleting a document
		assertEquals("failed to return true when deleting a document",true,store.deleteDocument(this.uri1));
		//should return false if I try to delete the same doc again
		assertEquals("failed to return false when trying to delete that which was already deleted",false,store.deleteDocument(this.uri1));
		//should return false if I try to delete something that was never there to begin with
		assertEquals("failed to return false when trying to delete that which was never there to begin with",false,store.deleteDocument(this.uri2));
	}

	@Test
	public void stage3Search(){
		DocumentStore store = new DocumentStoreImpl();
		store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
		store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);

		List<String> results = store.search("plain");
		assertEquals("expected 2 matches, only received " + results.size(),2,results.size());
		results = store.search("missing");
		assertEquals("expected 0 matches, received " + results.size(),0,results.size());
	}
	@Test
	public void stage3SearchPDFs(){
		DocumentStore store = new DocumentStoreImpl();
		store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData2),this.uri2, DocumentStore.DocumentFormat.PDF);

		List<byte[]> results = store.searchPDFs("pdf");
		assertEquals("expected 2 matches, only received " + results.size(),2,results.size());
		results = store.searchPDFs("missing");
		assertEquals("expected 0 matches, received " + results.size(),0,results.size());
	}

	@Test
	public void stage3DeleteAll(){
		DocumentStore store = new DocumentStoreImpl();
		store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
		store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
		//search, get results
		List<String> results = store.search("plain");
		assertEquals("expected 2 matches, only received " + results.size(),2,results.size());
		//delete all, get no matches
		store.deleteAll("plain");
		results = store.search("plain");
		assertEquals("expected 0 matches, received " + results.size(),0,results.size());
	}

	@Test
	public void stage3SearchByPrefix(){
		DocumentStore store = new DocumentStoreImpl();
		store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
		store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
		//search, get results
		List<String> results = store.searchByPrefix("str");
		assertEquals("expected 2 matches, only received " + results.size(),2,results.size());
		results = store.searchByPrefix("comp");
		assertEquals("expected 1 match, only received " + results.size(),1,results.size());
		results = store.searchByPrefix("doc2");
		assertEquals("expected 1 match, only received " + results.size(),1,results.size());
		results = store.searchByPrefix("blah");
		assertEquals("expected 0 match, received " + results.size(),0,results.size());
	}
	@Test
	public void stage3SearchPDFsByPrefix(){
		DocumentStore store = new DocumentStoreImpl();
		store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData2),this.uri2, DocumentStore.DocumentFormat.PDF);
		//search, get results
		List<byte[]> results = store.searchPDFsByPrefix("pd");
		assertEquals("expected 2 matches, only received " + results.size(),2,results.size());
		results = store.searchPDFsByPrefix("ado");
		assertEquals("expected 1 match, only received " + results.size(),1,results.size());
		results = store.searchPDFsByPrefix("blah");
		assertEquals("expected 0 match, received " + results.size(),0,results.size());
	}

	@Test
	public void stage3DeleteAllWithPrefix(){
		DocumentStore store = new DocumentStoreImpl();
		store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData2),this.uri2, DocumentStore.DocumentFormat.PDF);
		//search, get results
		List<byte[]> results = store.searchPDFsByPrefix("pd");
		assertEquals("expected 2 matches, only received " + results.size(),2,results.size());
		//delete all starting with pd
		store.deleteAllWithPrefix("pd");
		//search again, should be empty
		results = store.searchPDFsByPrefix("pd");
		assertEquals("expected 0 matches, received " + results.size(),0,results.size());
	}

	@Test
	public void stage3TestSearchByKeyword() {
		//put the four docs into the doc store
		DocumentStore store = new DocumentStoreImpl();
		store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
		//search by keyword
		List<String> results = store.search("adobe");
		assertEquals("search should've returned 2 results",2,results.size());
		//make sure we have the correct two documents
		boolean found1, found3;
		found1 = found3 = false;
		String lower1 = this.pdfTxt1.toLowerCase();
		String lower3 = this.pdfTxt3.toLowerCase();
		for(String txt:results){
			if(txt.toLowerCase().equals(lower1)) {
				found1 = true;
			}else if(txt.toLowerCase().equals(lower3)){
				found3 = true;
			}
		}
		assertTrue("should've found doc1 and doc3",found1 && found3);
	}

	@Test
	public void stage3TestSearchByPrefix() {
		//put the four docs into the doc store
		DocumentStore store = new DocumentStoreImpl();
		store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
		//search by prefix
		List<String> results = store.searchByPrefix("ha");
		assertEquals("search should've returned 2 results",2,results.size());
		//make sure we have the correct two documents
		boolean found1, found3;
		found1 = found3 = false;
		String lower1 = this.pdfTxt1.toLowerCase();
		String lower3 = this.pdfTxt3.toLowerCase();
		for(String txt:results){
			if(txt.toLowerCase().equals(lower1)) {
				found1 = true;
			}else if(txt.toLowerCase().equals(lower3)){
				found3 = true;
			}
		}
		assertTrue("should've found doc1 and doc3",found1 && found3);
	}

	@Test
	public void stage3TestDeleteAllByKeyword() {
		//put the four docs into the doc store
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
		//delete by keyword
		store.deleteAll("adobe");
		//search by keyword
		List<String> results = store.search("adobe");
		assertEquals("search should've returned 0 results",0,results.size());
		//make sure the correct two documents were deleted
		assertNull("doc1 should've been deleted",store.getDocument(this.uri1));
		assertNull("doc3 should've been deleted",store.getDocument(this.uri3));
		//make sure the other two documents were NOT deleted
		assertNotNull("doc2 should NOT been deleted",store.getDocument(this.uri2));
		assertNotNull("doc4 should NOT been deleted",store.getDocument(this.uri4));
	}

	@Test
	public void stage3TestDeleteAllByPrefix() {
		//put the four docs into the doc store
		DocumentStoreImpl store = new DocumentStoreImpl();
		store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
		store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
		String prefix = "ha";
		//delete by prefix
		store.deleteAllWithPrefix(prefix);
		//search by keyword
		List<String> results = store.searchByPrefix(prefix);
		assertEquals("search should've returned 0 results",0,results.size());
		//make sure the correct two documents were deleted
		assertNull("doc1 should've been deleted",store.getDocument(this.uri1));
		assertNull("doc3 should've been deleted",store.getDocument(this.uri3));
		//make sure the other two documents were NOT deleted
		assertNotNull("doc2 should NOT been deleted",store.getDocument(this.uri2));
		assertNotNull("doc4 should NOT been deleted",store.getDocument(this.uri4));
	}
}