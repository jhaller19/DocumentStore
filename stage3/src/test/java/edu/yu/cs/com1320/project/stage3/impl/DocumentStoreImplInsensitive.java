package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.stage3.DocumentStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class DocumentStoreImplInsensitive {

	DocumentStoreImpl store = new DocumentStoreImpl();

	@Test
	public void searchDocs() throws URISyntaxException {
		String text = "this is cOOl cool";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "I am cool cOOl cool";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		List<String> list = new ArrayList<>();
		list.add("I am cool cOOl cool");
		list.add("this is cOOl cool");

		assertEquals(list, store.search("Cool"));
	}
	@Test
	public void searchDocsIncesistive() throws URISyntaxException {
		String text = "this is cOOl cool";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "I am cooL cooL cooL";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		List<String> list = new ArrayList<>();
		list.add("I am cooL cooL cooL");
		list.add("this is cOOl cool");

		assertEquals(list, store.search("cool"));
	}


	@Test
	public void searchDocsPrefix() throws URISyntaxException {
		String text = "unDoable is annoying uNdo undO";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "I am unDOable undO undo UNDO";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		String text3 = "Nothing";
		InputStream input3 = new ByteArrayInputStream(text3.getBytes());
		URI uri3 =  new URI("3");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		List<String> list = new ArrayList<>();
		list.add("I am unDOable undO undo UNDO");
		list.add("unDoable is annoying uNdo undO");

		assertEquals(list , store.searchByPrefix("undo"));
	}


	@Test
	public void searchPDFs() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "CAPs 1";
		InputStream input = createPDFinput(text);
		URI uri =  new URI("1");
		//
		String text2 = "CaPs 2";
		InputStream input2 = createPDFinput(text2);
		URI uri2 =  new URI("2");
		//
		String text3 = "nothing";
		InputStream input3 = createPDFinput(text3);
		URI uri3 =  new URI("3");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		List<String> list = new ArrayList<>();
		list.add("CAPs 1");
		list.add("CaPs 2");
		assertEquals(2 , store.searchPDFs("caPs").size());
		//System.out.println(store.searchPDFs("1"));
	}



	@Test
	public void searchPDFsPrefix() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "this is Undoable";
		InputStream input = createPDFinput(text);
		URI uri =  new URI("1");
		//
		String text2 = "i undOable am";
		InputStream input2 = createPDFinput(text2);
		URI uri2 =  new URI("2");
		//
		String text3 = "nothing";
		InputStream input3 = createPDFinput(text3);
		URI uri3 =  new URI("3");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);

		assertEquals(2 , store.searchPDFsByPrefix("unDo").size());
		//System.out.println(store.searchPDFsByPrefix("undo"));

	}


	@Test
	public void deleteAll() throws URISyntaxException {
		String text = "Delete All";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "Delete all";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		List<String> list = new ArrayList<>();
		list.add("Delete All");
		list.add("Delete all");
		assertEquals(list , store.search("aLL"));
		assertEquals(list , store.search("delete"));

		Set<URI> set = new HashSet<>();
		set.add(uri);
		set.add(uri2);
		assertEquals(set , store.deleteAll("aLl"));
		List<String> emptySet = new ArrayList<>();
		assertEquals(emptySet , store.search("deletE"));
		assertEquals(emptySet , store.search("All"));
	}

	@Test
	public void deleteAllWithPrefix() throws URISyntaxException {
		String text = "Delete All";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "Delete all";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		List<String> list = new ArrayList<>();
		list.add("Delete All");
		list.add("Delete all");
		assertEquals(list , store.search("aLL"));
		assertEquals(list , store.search("delete"));

		Set<URI> set = new HashSet<>();
		set.add(uri);
		set.add(uri2);
		assertEquals(set , store.deleteAllWithPrefix("DEL"));
		List<String> emptySet = new ArrayList<>();
		assertEquals(emptySet , store.search("deletE"));
		assertEquals(emptySet , store.search("All"));

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

	private String pdfToString(byte[] array){
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

		return text;
	}

}