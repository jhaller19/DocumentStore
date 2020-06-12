package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.stage4.DocumentStore;
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

public class DocumentStoreTrieMethodTest {

	DocumentStoreImpl store = new DocumentStoreImpl();

	@Test
	public void searchDocs() throws URISyntaxException {
		String text = "this is cool cool";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "I am cool co()ol co()ol";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		List<String> list = new ArrayList<>();
		list.add("I am cool co()ol co()ol");
		list.add("this is cool cool");

		assertEquals(list, store.search("cool"));
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
	public void searchSORTEDDocs() throws URISyntaxException {
		String text = "1";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "1 1 1 1 1 1";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		String text3 = "1 1";
		InputStream input3 = new ByteArrayInputStream(text3.getBytes());
		URI uri3 =  new URI("3");
		//
		String text4 = "1 1 1";
		InputStream input4 = new ByteArrayInputStream(text4.getBytes());
		URI uri4 =  new URI("4");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);

		List<String> list = new ArrayList<>();
		list.add("1 1 1 1 1 1");
		list.add("1 1 1");
		list.add("1 1");
		list.add("1");
		assertEquals(list, store.search("1"));
	}

	@Test
	public void searchDocsPrefix() throws URISyntaxException {
		String text = "un()doable is annoying un()do un()do";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "I am undoable undo";
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

		list.add("un()doable is annoying un()do un()do");
		list.add("I am undoable undo");

		assertEquals(list , store.searchByPrefix("undo"));
	}

	@Test
	public void searchSORTEDPrefixDocs() throws URISyntaxException {
		String text = "1g";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "1a 1b 1c 1d 1e 1f";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		String text3 = "1h 1i";
		InputStream input3 = new ByteArrayInputStream(text3.getBytes());
		URI uri3 =  new URI("3");
		//
		String text4 = "1j 1k 1l";
		InputStream input4 = new ByteArrayInputStream(text4.getBytes());
		URI uri4 =  new URI("4");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);

		List<String> list = new ArrayList<>();
		list.add("1a 1b 1c 1d 1e 1f");
		list.add("1j 1k 1l");
		list.add("1h 1i");
		list.add("1g");
		assertEquals(list, store.searchByPrefix("1"));

	}

	@Test
	public void searchPDFs() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "1";
		InputStream input = createPDFinput(text);
		URI uri =  new URI("1");
		//
		String text2 = "2 1";
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
		list.add("1");
		list.add("2 1");
		assertEquals(2 , store.searchPDFs("1").size());
		//System.out.println(store.searchPDFs("1"));
	}

	@Test
	public void searchSORTEDpdf() throws URISyntaxException, IOException {
		String text = "1";
		InputStream input = createPDFinput(text);
		URI uri =  new URI("1");
		//
		String text2 = "1 1 1 1 1 1";
		InputStream input2 = createPDFinput(text2);
		URI uri2 =  new URI("2");
		//
		String text3 = "1 1";
		InputStream input3 = createPDFinput(text3);
		URI uri3 =  new URI("3");
		//
		String text4 = "1 1 1";
		InputStream input4 = createPDFinput(text4);
		URI uri4 =  new URI("4");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.PDF);

		List<String> list = new ArrayList<>();
		list.add("1 1 1 1 1 1");
		list.add("1 1 1");
		list.add("1 1");
		list.add("1");
		List<String> returnList = new ArrayList<>();
		for(byte[] a : store.searchPDFs("1")){
			returnList.add(pdfToString(a));
		}
		assertEquals(list , returnList);
	}

	@Test
	public void searchPDFsPrefix() throws URISyntaxException, IOException {
		DocumentStoreImpl store = new DocumentStoreImpl();
		String text = "this is undoable";
		InputStream input = createPDFinput(text);
		URI uri =  new URI("1");
		//
		String text2 = "i undoable am";
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

		assertEquals(2 , store.searchPDFsByPrefix("undo").size());
		//System.out.println(store.searchPDFsByPrefix("undo"));

	}

	@Test
	public void searchSORTEDprefixPDf() throws URISyntaxException, IOException {
		String text = "1a";
		InputStream input = createPDFinput(text);
		URI uri =  new URI("1");
		//
		String text2 = "1b 1c 1d 1e 1f 1g";
		InputStream input2 = createPDFinput(text2);
		URI uri2 =  new URI("2");
		//
		String text3 = "1h 1i";
		InputStream input3 = createPDFinput(text3);
		URI uri3 =  new URI("3");
		//
		String text4 = "1j 1k 1l";
		InputStream input4 = createPDFinput(text4);
		URI uri4 =  new URI("4");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.PDF);
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.PDF);

		List<String> list = new ArrayList<>();
		list.add("1b 1c 1d 1e 1f 1g");
		list.add("1j 1k 1l");
		list.add("1h 1i");
		list.add("1a");
		List<String> list2 = new ArrayList<>();
		for(byte[] a : store.searchPDFsByPrefix("1")){
			list2.add(pdfToString(a));
		}
		assertEquals(list , list2);
	}

	@Test
	public void deleteAll() throws URISyntaxException {
		String text = "1 2 3 4 5";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "1 6 7 8 9";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		List<String> list = new ArrayList<>();
		list.add("1 2 3 4 5");
		list.add("1 6 7 8 9");
		assertEquals(list , store.search("1"));
		list.remove("1 6 7 8 9");
		assertEquals(list , store.search("2"));
		assertEquals(list , store.search("3"));
		assertEquals(list , store.search("4"));
		assertEquals(list , store.search("5"));
		list.remove("1 2 3 4 5");
		list.add("1 6 7 8 9");
		assertEquals(list , store.search("6"));
		assertEquals(list , store.search("7"));
		assertEquals(list , store.search("8"));
		assertEquals(list , store.search("9"));

		Set<URI> set = new HashSet<>();
		set.add(uri);
		set.add(uri2);
		assertEquals(set , store.deleteAll("1"));
		List<String> emptySet = new ArrayList<>();
		assertEquals(emptySet , store.search("1"));
		assertEquals(emptySet , store.search("2"));
		assertEquals(emptySet , store.search("3"));
		assertEquals(emptySet , store.search("4"));
		assertEquals(emptySet , store.search("5"));
		assertEquals(emptySet , store.search("6"));
		assertEquals(emptySet , store.search("7"));
		assertEquals(emptySet , store.search("8"));
		assertEquals(emptySet , store.search("9"));
	}

	@Test
	public void deleteAllWithPrefix() throws URISyntaxException {
		String text = "this is undoable";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "I undoable cool";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);

		Set<URI> set = new HashSet<>();
		set.add(uri);
		set.add(uri2);
		assertEquals(set , store.deleteAllWithPrefix("undo"));
		List<String> emptySet = new ArrayList<>();
		assertEquals(emptySet , store.search("is"));
		assertEquals(emptySet , store.search("this"));
		assertEquals(emptySet , store.search("i"));
		assertEquals(emptySet , store.search("I"));
		assertEquals(emptySet , store.search("cool"));

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
	@Test
	public void putNullTrie() throws URISyntaxException {
		String text = "this cool";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri = new URI("1");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		List<String> list = new ArrayList<>();
		List<String> elist = new ArrayList<>();

		list.add("this cool");
		assertEquals(list, store.search("this"));
		store.putDocument(null , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals(elist, store.search("this"));

	}

	@Test
	public void deleteAllGrrr() throws URISyntaxException {
		String text = "this cool";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "this is not cool";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);

		Set<URI> set = new HashSet<>();
		set.add(uri);
		set.add(uri2);
		assertEquals(set , store.deleteAll("cool"));
		List<String> emptySet = new ArrayList<>();
		assertEquals(emptySet , store.search("this"));

	}




}