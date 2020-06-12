package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.DocumentStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BaseDirNonNullTest {

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

	private File nBaseDir;


	@Before
	public void init() throws Exception {
		this.nBaseDir = new File("C:" + File.separator + "Users" + File.separator + "jhall" + File.separator + "Documents");

		//init possible values for doc1
		this.uri1 = new URI("https://uri1.com/doc1");
		this.txt1 = "doc1";
		this.input1 = new ByteArrayInputStream(txt1.getBytes());
		//this.pdfTxt1 = "This is some PDF text for doc1, hat tip to Adobe.";
		//this.pdfData1 = textToPdfData(this.pdfTxt1);

		this.uri2 = new URI("https://uri2.com/doc2");
		this.txt2 = "doc2";
		this.input2 = new ByteArrayInputStream(txt2.getBytes());

		this.uri3 = new URI("https://uri3.com/doc3");
		this.txt3 = "doc3";
		this.input3 = new ByteArrayInputStream(txt3.getBytes());

		this.uri4 = new URI("https://uri4.com/doc4");
		this.txt4 = "doc4";
		this.input4 = new ByteArrayInputStream(txt4.getBytes());

		this.uri5 = new URI("https://uri15.com/doc5");
		this.txt5 = "doc5";
		this.input5 = new ByteArrayInputStream(txt5.getBytes());

		this.uri6 = new URI("https://uri6.com/doc6");
		this.txt6 = "this is an undoable 1";
		this.input6 = new ByteArrayInputStream(txt6.getBytes());

		this.uri7 = new URI("https://uri7.com/doc7");
		this.txt7 = "this is an undoable 2";
		this.input7 = new ByteArrayInputStream(txt7.getBytes());

	}

	private File expectedFile(URI uri){
		return new File(nBaseDir + File.separator + uri.getHost() + uri.getPath() + ".json");
	}

	private void deleteFileAndEmptyDirs(File file){
		if(file.getPath().endsWith(nBaseDir.getPath())) {
			return;
		}
		if (file.isFile()) {
			file.delete();
		} else if(file.isDirectory()) {
			file.delete();
		}
		deleteFileAndEmptyDirs(file.getParentFile());
	}


	/////////

	@Test
	public void setAfterTest() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.setMaxDocumentCount(1);

		assertTrue(expectedFile(uri1).exists());
		assertEquals(1 , store.getDocsUsed());
		assertEquals(null , store.getDocument(uri1));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
		assertTrue(expectedFile(uri2).exists());
		assertTrue(!expectedFile(uri1).exists());
		assertEquals(1 , store.getDocsUsed());
		assertEquals(null , store.getDocument(uri2));
	}

	@Test
	public void getDocTextReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.getDocumentAsTxt(uri1);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri2));
		assertTrue(expectedFile(uri2).exists());
		assertEquals(2 , store.getDocsUsed());
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));
	}

	@Test
	public void getDocTextReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));

	}

	@Test
	public void getDocPDFReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.getDocumentAsPdf(uri1);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri2));
		assertTrue(expectedFile(uri2).exists());
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
	}

	@Test
	public void getDocPDFReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
	}

	@Test
	public void searchReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.search("doc1");
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri2));
		assertTrue(expectedFile(uri2).exists());
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
	}
	@Test
	public void searchReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri1));
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
	}

	@Test
	public void searchPdfReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.searchPDFs("doc1");
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri2));
		assertTrue(expectedFile(uri2).exists());
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
		assertTrue(!expectedFile(uri1).exists());

	}
	@Test
	public void searchPdfReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri1));
		assertTrue(expectedFile(uri1).exists());
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
	}

	@Test
	public void searchPrefixReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.searchByPrefix("doc1");
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri2));
		assertTrue(expectedFile(uri2).exists());
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));

	}
	@Test
	public void searchPrefixReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri1));
		assertTrue(expectedFile(uri1).exists());
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));

	}

	@Test
	public void searchPDFPrefixReheapify1() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.searchPDFsByPrefix("doc1");
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri2));
		assertTrue(expectedFile(uri2).exists());
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
	}
	@Test
	public void searchPDFPrefixReheapify2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri1));
		assertTrue(expectedFile(uri1).exists());
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
	}

	@Test
	public void simpleDeleteInHeap() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.deleteDocument(uri2);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri1));
		assertTrue(expectedFile(uri1).exists());
		assertEquals(null , store.getDocument(uri2));
		assertTrue(!expectedFile(uri2).exists());
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));
		assertEquals("doc4" , store.getDocumentAsTxt(uri4));
	}
	@Test
	public void simpleDeleteAllInHeap() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.deleteAll("doc2");
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri1));
		assertTrue(expectedFile(uri1).exists());
		assertEquals(null , store.getDocument(uri2));
		assertTrue(!expectedFile(uri2).exists());
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));
		assertEquals("doc4" , store.getDocumentAsTxt(uri4));
	}

	@Test
	public void replaceTest() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri1 , DocumentStore.DocumentFormat.TXT);
		//assertEquals("doc1" , store.getDocumentAsTxt(uri1));
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		//assertEquals("doc2" , store.getDocumentAsTxt(uri1));
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null, store.getDocument(uri1));
		assertTrue(expectedFile(uri1).exists());
		assertEquals("doc3", store.getDocumentAsTxt(uri3));
		assertEquals("doc4", store.getDocumentAsTxt(uri4));
	}
	@Test
	public void putSameDoc() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		String a = "doc1";
		InputStream i = new ByteArrayInputStream(a.getBytes());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(i , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);

		assertEquals(null, store.getDocument(uri2));
		assertTrue(expectedFile(uri2).exists());
		assertEquals("doc1", store.getDocumentAsTxt(uri1));
		assertTrue(!expectedFile(uri1).exists());
		assertEquals("doc3", store.getDocumentAsTxt(uri3));
		assertTrue(!expectedFile(uri3).exists());
	}

	/////////////////////////Lists///////////////////////////////////////////
	@Test
	public void searchList() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.search("this");
		assertEquals(store.getDocument(uri6).getLastUseTime() , store.getDocument(uri7).getLastUseTime());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri3));
		assertTrue(expectedFile(uri3).exists());
		assertEquals("this is an undoable 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this is an undoable 2" , store.getDocumentAsTxt(uri7));
		assertTrue(!expectedFile(uri6).exists());
		assertTrue(!expectedFile(uri7).exists());

	}
	@Test
	public void searchPDFList() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.searchPDFs("this");
		assertEquals(store.getDocument(uri6).getLastUseTime() , store.getDocument(uri7).getLastUseTime());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri3));
		assertTrue(expectedFile(uri3).exists());
		assertEquals("this is an undoable 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this is an undoable 2" , store.getDocumentAsTxt(uri7));
		assertTrue(!expectedFile(uri6).exists());
		assertTrue(!expectedFile(uri7).exists());

	}
	@Test
	public void searchPrefixList() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.searchByPrefix("undo");
		assertEquals(store.getDocument(uri6).getLastUseTime() , store.getDocument(uri7).getLastUseTime());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri3));
		assertTrue(expectedFile(uri3).exists());
		assertEquals("this is an undoable 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this is an undoable 2" , store.getDocumentAsTxt(uri7));
		assertTrue(!expectedFile(uri6).exists());
		assertTrue(!expectedFile(uri7).exists());

	}

	@Test
	public void searchPDFPrefixList() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.searchPDFsByPrefix("undo");
		assertEquals(store.getDocument(uri6).getLastUseTime() , store.getDocument(uri7).getLastUseTime());
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri3));
		assertTrue(expectedFile(uri3).exists());
		assertEquals("this is an undoable 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this is an undoable 2" , store.getDocumentAsTxt(uri7));
		assertTrue(!expectedFile(uri6).exists());
		assertTrue(!expectedFile(uri7).exists());

	}


	/////Stage 5////////
	@Test
	public void getText() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(1);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		assertTrue(expectedFile(uri1).exists());
		assertEquals(1 , store.getDocsUsed());
		assertEquals(null , store.getDocument(uri1));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
		assertTrue(expectedFile(uri2).exists());
		assertTrue(!expectedFile(uri1).exists());
		assertEquals(1 , store.getDocsUsed());
		assertEquals(null , store.getDocument(uri2));
	}

	@Test
	public void getText2() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		//1 is gone
		assertTrue(expectedFile(uri1).exists());
		assertEquals(2 , store.getDocsUsed());
		assertEquals(null , store.getDocument(uri1));

		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));

		//Bring 1 back
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
		assertTrue(expectedFile(uri2).exists());
		assertTrue(!expectedFile(uri1).exists());
		assertEquals(2 , store.getDocsUsed());
		assertEquals(null , store.getDocument(uri2));

	}
	@Test
	public void getPdf() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(1);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		assertTrue(expectedFile(uri1).exists());
		assertEquals(1 , store.getDocsUsed());
		assertEquals(null , store.getDocument(uri1));
		assertEquals("doc1" , pdfToString(store.getDocumentAsPdf(uri1)));
		assertTrue(expectedFile(uri2).exists());
		assertTrue(!expectedFile(uri1).exists());
		assertEquals(1 , store.getDocsUsed());
		assertEquals(null , store.getDocument(uri2));
	}

	@Test
	public void searchTest() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(1);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		assertTrue(expectedFile(uri1).exists());
		assertEquals(1 , store.getDocsUsed());
		assertEquals(null , store.getDocument(uri1));
		List<String> list = new ArrayList<>();
		list.add("doc1");
		assertEquals(list, store.search("doc1"));
		assertTrue(expectedFile(uri2).exists());
		assertTrue(!expectedFile(uri1).exists());
		assertEquals(1 , store.getDocsUsed());
		assertEquals(null , store.getDocument(uri2));
	}

	@Test
	public void searchPrefixTest() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(1);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		assertTrue(expectedFile(uri1).exists());
		assertEquals(1 , store.getDocsUsed());
		assertEquals(null , store.getDocument(uri1));
		List<String> list = new ArrayList<>();
		list.add("doc1");
		assertEquals(list, store.searchByPrefix("doc1"));
		assertTrue(expectedFile(uri2).exists());
		assertTrue(!expectedFile(uri1).exists());
		assertEquals(1 , store.getDocsUsed());
		assertEquals(null , store.getDocument(uri2));

	}

	@Test
	public void searchPDFTest() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(1);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		assertTrue(expectedFile(uri1).exists());
		assertEquals(1 , store.getDocsUsed());
		assertEquals(null , store.getDocument(uri1));
		List<String> list = new ArrayList<>();
		list.add("doc1");
		List<byte[]> actualList = store.searchPDFs("doc1");
		List<String> realList = new ArrayList<>();
		for(byte[] a : actualList){
			String x = pdfToString(a);
			realList.add(x);
		}
		assertEquals(list, realList);
		assertTrue(expectedFile(uri2).exists());
		assertTrue(!expectedFile(uri1).exists());
		assertEquals(1 , store.getDocsUsed());
		assertEquals(null , store.getDocument(uri2));

	}


	/////////////////////////UNDO////////////////////////////////////
	@Test
	public void undoPutDocsUsed() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.undo();
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri2));
		assertEquals("doc1" , store.getDocumentAsTxt(uri1));
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));
	}
	@Test
	public void undoDeleteDocsUsed() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.deleteDocument(uri1);
		store.undo();
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri1));
		assertTrue(expectedFile(uri1).exists());
		assertEquals("doc2" , store.getDocumentAsTxt(uri2));
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));
	}

	@Test
	public void undoReplaceDocsUsed() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.undo();
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri1));
		assertTrue(expectedFile(uri1).exists());
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("doc3" , store.getDocumentAsTxt(uri3));
		assertEquals("doc4" , store.getDocumentAsTxt(uri4));
	}

	@Test
	public void undoDeleteAll() throws URISyntaxException {
		DocumentStoreImpl store = new DocumentStoreImpl(nBaseDir);
		store.setMaxDocumentCount(3);
		store.putDocument(input6 , uri6 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input7 , uri7 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.deleteAll("this");
		store.undo();
		store.putDocument(input5 , uri5 , DocumentStore.DocumentFormat.TXT);

		assertEquals(null , store.getDocument(uri3));
		assertTrue(expectedFile(uri3).exists());
		assertTrue(!expectedFile(uri6).exists());
		assertTrue(!expectedFile(uri7).exists());

		assertEquals("this is an undoable 1" , store.getDocumentAsTxt(uri6));
		assertEquals("this is an undoable 2" , store.getDocumentAsTxt(uri7));

	}

	/*@Test(expected = IllegalStateException.class)
	public void deleteFromStackCheck1() {
		DocumentStoreImpl store = new DocumentStoreImpl(null);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);

		store.undo(uri1);

	}
	@Test(expected = IllegalStateException.class)
	public void deleteFromStackCheck2() {
		DocumentStoreImpl store = new DocumentStoreImpl(null);
		store.setMaxDocumentCount(2);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);

		store.undo();
		store.undo();
		store.undo();
	}

	@Test(expected = IllegalStateException.class)
	public void deleteFromCommandSet() {
		DocumentStoreImpl store = new DocumentStoreImpl(null);
		store.setMaxDocumentCount(2);
		store.putDocument(input6, uri6, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input7, uri7, DocumentStore.DocumentFormat.TXT);
		store.deleteAll("this");
		store.putDocument(input6, uri6, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input7, uri7, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3, uri3, DocumentStore.DocumentFormat.TXT);
		store.undo(uri6);
	}*/


	/////


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

	@After
	public void after(){
		File file1 = new File(nBaseDir + File.separator + uri1.getHost() + uri1.getPath() + ".json");
		File file2 = new File(nBaseDir + File.separator + uri2.getHost() + uri2.getPath()+ ".json");
		File file3 = new File(nBaseDir + File.separator + uri3.getHost() + uri3.getPath()+ ".json");
		File file6 = new File(nBaseDir + File.separator + uri6.getHost() + uri6.getPath()+ ".json");

		deleteFileAndEmptyDirs(file1);
		deleteFileAndEmptyDirs(file2);
		deleteFileAndEmptyDirs(file3);
		deleteFileAndEmptyDirs(file6);

	}


}